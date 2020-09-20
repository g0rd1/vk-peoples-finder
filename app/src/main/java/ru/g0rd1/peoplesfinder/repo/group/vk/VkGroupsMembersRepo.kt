package ru.g0rd1.peoplesfinder.repo.group.vk

import io.reactivex.Single
import ru.g0rd1.peoplesfinder.model.User

interface VkGroupsMembersRepo {

    fun getGroupMembers(groupId: String, count: Int, offset: Int = 0): Single<List<User>>

}