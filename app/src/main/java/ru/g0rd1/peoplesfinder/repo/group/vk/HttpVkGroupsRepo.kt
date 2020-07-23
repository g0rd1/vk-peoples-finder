package ru.g0rd1.peoplesfinder.repo.group.vk

import io.reactivex.Single
import ru.g0rd1.peoplesfinder.apiservice.ApiClient
import ru.g0rd1.peoplesfinder.apiservice.QueueApiClient
import ru.g0rd1.peoplesfinder.model.Group
import ru.g0rd1.peoplesfinder.model.User
import ru.g0rd1.peoplesfinder.repo.access.VKAccessRepo
import javax.inject.Inject

class HttpVkGroupsRepo @Inject constructor(
    _apiClient: ApiClient,
    private val VKAccessRepo: VKAccessRepo
) : VkGroupsRepo {

    private val apiClient: QueueApiClient = QueueApiClient(_apiClient)

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun getGroupsByQuery(query: String, _count: Int?): Single<List<Group>> {
        val count = _count ?: DEFAULT_GROUP_COUNT
        return apiClient.getGroupsByQuery(
            query = query,
            count = count,
            accessToken = VKAccessRepo.getUserToken()
        )
    }

    override fun getGroups(userId: String): Single<List<Group>> =
        apiClient.getGroups(
            userId = userId,
            accessToken = VKAccessRepo.getUserToken(),
            count = DEFAULT_GROUP_COUNT
        ).flatMap {
            when {
                it.error != null -> {
                    Single.error(it.error)
                }
                it.response != null -> {
                    Single.just(it.response.items)
                }
                else -> {
                    throw IllegalArgumentException("Response and error can't be both null")
                }
            }
        }

    override fun getGroupMembers(groupId: String, count: Int, offset: Int): Single<List<User>> {
        if (count % 25L != 0L) throw IllegalArgumentException("the number should be divisible by $DEFAULT_STEPS_COUNT")
        val step = count / 25
        val code = ApiClient.getGroupMembersCode(
            groupId = groupId,
            offset = offset,
            step = step,
            stepsCount = DEFAULT_STEPS_COUNT
        )
        return apiClient.getGroupMembers(
            code,
            accessToken = VKAccessRepo.getUserToken()
        )
            .flatMap {
                when {
                    it.error != null -> {
                        Single.error(it.error)
                    }
                    it.executeErrors?.isNotEmpty() == true -> {
                        Single.error(it.executeErrors.first())
                    }
                    it.response != null -> {
                        Single.just(it.response.flatMap { response -> response.items })
                    }
                    else -> {
                        throw IllegalArgumentException("Response and error can't be both null")
                    }
                }
            }
    }

    companion object {
        const val DEFAULT_GROUP_COUNT = 1000
        const val DEFAULT_STEPS_COUNT = 25
    }
}