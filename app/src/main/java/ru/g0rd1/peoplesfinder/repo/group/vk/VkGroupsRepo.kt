package ru.g0rd1.peoplesfinder.repo.group.vk

import io.reactivex.Single
import ru.g0rd1.peoplesfinder.model.Group
import ru.g0rd1.peoplesfinder.model.User

interface VkGroupsRepo {

    fun getGroupsByQuery(query: String, count: Int? = null): Single<List<Group>>

    fun getGroups(userId: String): Single<List<Group>>

    fun getGroupMembers(groupId: String, count: Int, offset: Int = 0): Single<List<User>>

}