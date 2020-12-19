package ru.g0rd1.peoplesfinder.repo.group.vk

import io.reactivex.Single
import ru.g0rd1.peoplesfinder.model.GetGroupMembersResult
import ru.g0rd1.peoplesfinder.model.Group

interface VkGroupsRepo {

    fun getGroups(): Single<List<Group>>
    fun getGroupMembers(groupId: String, count: Int, offset: Int = 0): Single<GetGroupMembersResult>

}