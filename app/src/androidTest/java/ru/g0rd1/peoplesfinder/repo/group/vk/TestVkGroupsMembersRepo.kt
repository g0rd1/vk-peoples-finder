package ru.g0rd1.peoplesfinder.repo.group.vk

import io.reactivex.Single
import ru.g0rd1.peoplesfinder.model.User
import ru.g0rd1.peoplesfinder.repo.user.UserTestUtil

class TestVkGroupsMembersRepo : VkGroupsMembersRepo {

    override fun getGroupMembers(groupId: String, count: Int, offset: Int): Single<List<User>> {
        return Single.just((offset until offset + count).map { UserTestUtil.getTestUser(it) })
    }

}