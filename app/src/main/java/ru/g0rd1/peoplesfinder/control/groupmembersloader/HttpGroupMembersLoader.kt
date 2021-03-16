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
import java.util.*

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
                        .andThen(load(it.value.loadedMembersCount, it.value.membersCount))
                }
            }
        }
    }

    private fun load(offset: Int, membersCount: Int): Single<LoadResult> {
        return if (offset <= membersCount) {
            Single.fromCallable { accessDenied }.flatMap { accessDenied ->
                if (accessDenied) {
                    Single.just(LoadResult.Success)
                } else {
                    loadWithOffset(offset).flatMap { loadResult ->
                        if (loadResult == LoadResult.Success) {
                            load(
                                offset + DOWNLOAD_MEMBERS_STEP,
                                membersCount
                            )
                        } else {
                            Single.just(loadResult)
                        }
                    }
                }
            }
        } else {
            localGroupsRepo.updateAllMembersLoadedDate(groupId, Date()).andThen(
                Single.just(LoadResult.Success)
            )
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
                            is VkError.RateLimitReached -> Single.just(
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
        private const val DOWNLOAD_MEMBERS_STEP = 10000
    }

}