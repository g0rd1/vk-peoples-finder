package ru.g0rd1.peoplesfinder.control.groupmembersloader

import io.reactivex.Completable
import io.reactivex.Single
import ru.g0rd1.peoplesfinder.control.groupmembersloader.GroupMembersLoader.LoadResult
import ru.g0rd1.peoplesfinder.model.Optional
import ru.g0rd1.peoplesfinder.model.VkResult
import ru.g0rd1.peoplesfinder.model.error.VkError
import ru.g0rd1.peoplesfinder.repo.group.local.LocalGroupsRepo
import ru.g0rd1.peoplesfinder.repo.group.vk.VkGroupsRepo
import ru.g0rd1.peoplesfinder.repo.user.local.LocalUsersRepo
import java.time.LocalDate

class HttpGroupMembersLoader(
    private val groupId: Int,
    private val vkGroupsRepo: VkGroupsRepo,
    private val localUsersRepo: LocalUsersRepo,
    private val localGroupsRepo: LocalGroupsRepo,
) : GroupMembersLoader {

    @Volatile
    private var accessDenied = false

    override fun load(): Single<LoadResult> {
        return localGroupsRepo.get(groupId).flatMap {
            when (it) {
                is Optional.Empty -> Single.just(LoadResult.Error.Generic(Throwable("Нет группы с id $groupId")))
                is Optional.Value -> {
                    localGroupsRepo.updateHasAccessToMembers(groupId, true)
                        .andThen(
                            load(
                                it.value.loadedMembersCount,
                                it.value.membersCount,
                            )
                        )
                }
            }
        }
    }

    private fun load(lasOffset: Int, membersCount: Int): Single<LoadResult> {
        val singles = (lasOffset..membersCount step DOWNLOAD_MEMBERS_STEP).map { offset ->
            Single.fromCallable { accessDenied }.flatMap { accessDenied ->
                if (accessDenied) {
                    Single.just(LoadResult.Success)
                } else {
                    loadWithOffset(offset)
                }
            }
        }
        return Single.concat(singles)
            .skipWhile { it is LoadResult.Success }
            .first(LoadResult.Success)
            .flatMap {
                if (it is LoadResult.Success) {
                    localGroupsRepo.updateAllMembersLoadedDate(groupId, LocalDate.now()).andThen(
                        Single.just(LoadResult.Success)
                    )
                } else {
                    Single.just(it)
                }
            }
    }

    private fun loadWithOffset(offset: Int): Single<LoadResult> {
        return vkGroupsRepo.getGroupMembers(groupId.toString(), DOWNLOAD_MEMBERS_STEP, offset)
            .flatMap { getMembersResult ->
                when (getMembersResult) {
                    is VkResult.Success -> {
                        localUsersRepo.insertWithGroups(
                            getMembersResult.data.associateWith { listOf(groupId) }
                        ).andThen(
                            localGroupsRepo.updateLoadedMembersCount(
                                groupId,
                                offset + DOWNLOAD_MEMBERS_STEP
                            )
                        ).andThen(
                            Single.just(LoadResult.Success)
                        )
                    }
                    is VkResult.Error.ApiVk -> {
                        when (getMembersResult.error) {
                            is VkError.Unknown,
                            is VkError.RateLimitReached,
                            -> Single.just(
                                LoadResult.Error.Vk(getMembersResult.error)
                            )
                            is VkError.TooManyRequestsPerSecond -> loadWithOffset(offset)
                            is VkError.AccessDenied -> Completable.fromAction {
                                accessDenied = true
                            }
                                .andThen(
                                    localGroupsRepo.updateHasAccessToMembers(groupId, false)
                                )
                                .andThen(Single.just(LoadResult.Success))
                        }
                    }
                    is VkResult.Error.Generic -> {
                        Single.just(LoadResult.Error.Generic(getMembersResult.throwable))
                    }
                }
            }
    }

    companion object {
        private const val DOWNLOAD_MEMBERS_STEP = 1000
    }

}