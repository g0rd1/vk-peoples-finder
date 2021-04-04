package ru.g0rd1.peoplesfinder.control.groupmembersloader

import ru.g0rd1.peoplesfinder.common.PriorityQueueManager
import ru.g0rd1.peoplesfinder.repo.group.local.LocalGroupsRepo
import ru.g0rd1.peoplesfinder.repo.group.vk.VkGroupsRepo
import ru.g0rd1.peoplesfinder.repo.user.local.LocalUsersRepo
import javax.inject.Inject

class HttpGroupMembersLoaderFactory @Inject constructor(
    private val vkGroupsRepo: VkGroupsRepo,
    private val localUsersRepo: LocalUsersRepo,
    private val localGroupsRepo: LocalGroupsRepo
) : GroupMembersLoader.Factory {

    override fun create(groupId: Int): GroupMembersLoader =
        HttpGroupMembersLoader(
            groupId = groupId,
            vkGroupsRepo = vkGroupsRepo,
            localUsersRepo = localUsersRepo,
            localGroupsRepo = localGroupsRepo
        )
}