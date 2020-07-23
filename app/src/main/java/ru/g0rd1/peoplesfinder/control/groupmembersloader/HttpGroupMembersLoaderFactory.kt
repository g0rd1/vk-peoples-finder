package ru.g0rd1.peoplesfinder.control.groupmembersloader

import io.reactivex.Single
import ru.g0rd1.peoplesfinder.base.scheduler.Schedulers
import ru.g0rd1.peoplesfinder.repo.group.local.LocalGroupsRepo
import ru.g0rd1.peoplesfinder.repo.group.vk.VkGroupsRepo
import ru.g0rd1.peoplesfinder.repo.user.local.LocalUsersRepo
import timber.log.Timber
import javax.inject.Inject

class HttpGroupMembersLoaderFactory @Inject constructor(
    private val vkGroupsRepo: VkGroupsRepo,
    private val schedulers: Schedulers,
    private val localUsersRepo: LocalUsersRepo,
    private val localGroupsRepo: LocalGroupsRepo,
    private val regulator: GroupMembersLoader.Regulator
) : GroupMembersLoader.Factory {

    override fun create(groupId: Int): Single<GroupMembersLoader> {

        return localGroupsRepo.get(groupId).flatMap {
            val groupEntity = it.value
            return@flatMap if (groupEntity == null) {
                Single.error(Throwable("No group with id $groupId in local repo"))
            } else {
                Single.just(groupEntity)
            }
        }.map { groupEntity ->
            Timber.d("groupEntity: $groupEntity")
            val status = when {
                groupEntity.allMembersLoadedDate != null -> GroupMembersLoader.Status.FINISH
                groupEntity.loadedMembersCount == 0 -> GroupMembersLoader.Status.STOPPED
                else -> GroupMembersLoader.Status.PAUSED
            }
            HttpGroupMembersLoader(
                groupId = groupId,
                schedulers = schedulers,
                vkGroupsRepo = vkGroupsRepo,
                localUsersRepo = localUsersRepo,
                localGroupsRepo = localGroupsRepo,
                groupMembersCount = groupEntity.membersCount,
                regulator = regulator,
                status = status,
                loadedCount = groupEntity.loadedMembersCount,
                allMembersLoadedDate = groupEntity.allMembersLoadedDate
            )
        }

    }
}