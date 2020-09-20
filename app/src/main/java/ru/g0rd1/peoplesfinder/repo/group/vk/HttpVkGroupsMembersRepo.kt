package ru.g0rd1.peoplesfinder.repo.group.vk

import io.reactivex.Single
import ru.g0rd1.peoplesfinder.apiservice.ApiClient
import ru.g0rd1.peoplesfinder.model.User
import ru.g0rd1.peoplesfinder.repo.access.VKAccessRepo
import javax.inject.Inject

class HttpVkGroupsMembersRepo @Inject constructor(
    private val apiClient: ApiClient,
    private val VKAccessRepo: VKAccessRepo
) : VkGroupsMembersRepo {

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
        const val DEFAULT_STEPS_COUNT = 25
    }

}