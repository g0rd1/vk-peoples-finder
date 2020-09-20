package ru.g0rd1.peoplesfinder.repo.group.vk

import io.reactivex.Single
import ru.g0rd1.peoplesfinder.apiservice.ApiClient
import ru.g0rd1.peoplesfinder.model.Group
import ru.g0rd1.peoplesfinder.repo.access.VKAccessRepo
import javax.inject.Inject

class HttpVkGroupsRepo @Inject constructor(
    private val apiClient: ApiClient,
    private val VKAccessRepo: VKAccessRepo
) : VkGroupsRepo {

    private var groupsCache: MutableMap<String, List<Group>> = mutableMapOf()

    @Synchronized
    override fun getGroups(userId: String): Single<List<Group>> {
        if (groupsCache[userId] != null) return Single.just(groupsCache[userId])
        return apiClient.getGroups(
            userId = userId,
            accessToken = VKAccessRepo.getUserToken(),
            count = DEFAULT_GROUP_COUNT
        ).flatMap {
            when {
                it.error != null -> {
                    Single.error(it.error)
                }
                it.response != null -> {
                    groupsCache[userId] = it.response.items
                    Single.just(it.response.items)
                }
                else -> {
                    throw IllegalArgumentException("Response and error can't be both null")
                }
            }
        }
    }

    companion object {
        const val DEFAULT_GROUP_COUNT = 1000
    }
}