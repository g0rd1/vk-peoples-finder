package ru.g0rd1.peoplesfinder.repo.group.vk

import io.reactivex.Single
import ru.g0rd1.peoplesfinder.model.Group

class TestVkGroupsRepo : VkGroupsRepo {

    override fun getGroups(userId: String): Single<List<Group>> {
        TODO("Not yet implemented")
    }

}