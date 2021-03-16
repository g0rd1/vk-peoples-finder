package ru.g0rd1.peoplesfinder.apiservice

import io.reactivex.Single
import ru.g0rd1.peoplesfinder.apiservice.model.ApiCity
import ru.g0rd1.peoplesfinder.apiservice.model.ApiCountry
import ru.g0rd1.peoplesfinder.apiservice.model.ApiGroup
import ru.g0rd1.peoplesfinder.apiservice.model.ApiUser
import ru.g0rd1.peoplesfinder.apiservice.response.ApiVkResponse
import ru.g0rd1.peoplesfinder.common.PriorityQueueManager
import ru.g0rd1.peoplesfinder.repo.access.VKAccessRepo

class PriorityQueueApiClient(
    apiClient: ApiClient,
    private val vkAccessRepo: VKAccessRepo,
    private val priorityQueueManager: PriorityQueueManager
) : ApiClientDecorator(apiClient) {

    override fun getGroups(
        userId: Int,
        extended: Int,
        fields: String,
        offset: Int,
        count: Int,
        accessToken: String,
        version: String
    ): Single<ApiVkResponse<ApiGroup>> {
        return priorityQueueManager.getQueuedSingle(
            apiClient.getGroups(
                userId,
                extended,
                fields,
                offset,
                count,
                accessToken,
                version
            ),
            HIGH_PRIORITY
        )
    }

    override fun getGroupMembers(
        code: String,
        accessToken: String,
        version: String
    ): Single<ApiVkResponse<ApiUser>> {
        return priorityQueueManager.getQueuedSingle(
            apiClient.getGroupMembers(code, accessToken, version),
            LOW_PRIORITY
        )
    }

    override fun getCountries(
        needAll: Int,
        count: Int,
        accessToken: String,
        version: String
    ): Single<ApiVkResponse<ApiCountry>> {
        return priorityQueueManager.getQueuedSingle(
            apiClient.getCountries(needAll, count, accessToken, version),
            HIGH_PRIORITY
        )
    }

    override fun getCities(
        needAll: Int,
        count: Int,
        countryId: Int?,
        query: String?,
        accessToken: String,
        version: String
    ): Single<ApiVkResponse<ApiCity>> {
        return priorityQueueManager.getQueuedSingle(
            apiClient.getCities(needAll, count, countryId, query, accessToken, version),
            HIGH_PRIORITY
        )
    }

    companion object {
        private const val HIGH_PRIORITY = 0
        private const val LOW_PRIORITY = 1
    }

    override fun getAccessToken(): String = vkAccessRepo.getUserToken()
}