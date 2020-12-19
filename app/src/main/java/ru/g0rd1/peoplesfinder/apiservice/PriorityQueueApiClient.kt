package ru.g0rd1.peoplesfinder.apiservice

import io.reactivex.Single
import ru.g0rd1.peoplesfinder.apiservice.response.GetGroupMembersResponse
import ru.g0rd1.peoplesfinder.apiservice.response.GetGroupsResponse
import ru.g0rd1.peoplesfinder.apiservice.response.GetUserResponse
import ru.g0rd1.peoplesfinder.common.PriorityQueueManager

class PriorityQueueApiClient(
    apiClient: ApiClient,
    private val priorityQueueManager: PriorityQueueManager
) : ApiClientDecorator(apiClient) {

    override fun getGroups(
        userId: String,
        extended: Int,
        fields: String,
        offset: Int,
        count: Int,
        accessToken: String,
        version: String
    ): Single<GetGroupsResponse> {
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
    ): Single<GetGroupMembersResponse> {
        return priorityQueueManager.getQueuedSingle(
            apiClient.getGroupMembers(code, accessToken, version),
            LOW_PRIORITY
        )
    }

    override fun getUser(fields: String): Single<GetUserResponse> {
        return priorityQueueManager.getQueuedSingle(apiClient.getUser(fields), HIGH_PRIORITY)
    }

    companion object {
        private const val HIGH_PRIORITY = 0
        private const val LOW_PRIORITY = 1
    }
}