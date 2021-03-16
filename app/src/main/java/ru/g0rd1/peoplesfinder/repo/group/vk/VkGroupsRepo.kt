package ru.g0rd1.peoplesfinder.repo.group.vk

import io.reactivex.Single
import ru.g0rd1.peoplesfinder.model.Group
import ru.g0rd1.peoplesfinder.model.User
import ru.g0rd1.peoplesfinder.model.VkResult

interface VkGroupsRepo {

    fun getGroups(): Single<VkResult<List<Group>>>

    fun getGroupMembers(groupId: String, count: Int, offset: Int = 0): Single<VkResult<List<User>>>

}