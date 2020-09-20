package ru.g0rd1.peoplesfinder.apiservice

import io.reactivex.Completable
import io.reactivex.Single
import ru.g0rd1.peoplesfinder.apiservice.response.GetGroupMembersResponse
import ru.g0rd1.peoplesfinder.apiservice.response.GetGroupsResponse
import ru.g0rd1.peoplesfinder.apiservice.response.GetUserResponse
import java.util.concurrent.BlockingQueue
import java.util.concurrent.PriorityBlockingQueue

class PriorityQueueApiClient(apiClient: ApiClient) : ApiClientDecorator(apiClient) {

    private val queue: BlockingQueue<PrioritySingle<*>> =
        PriorityBlockingQueue<PrioritySingle<*>>(QUEUE_CAPACITY, compareBy { it.priority })

    override fun getGroups(
        userId: String,
        extended: Int,
        fields: String,
        offset: Int,
        count: Int,
        accessToken: String,
        version: String
    ): Single<GetGroupsResponse> {
        return getSingleWithOfferToQueue(
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
        return getSingleWithOfferToQueue(
            apiClient.getGroupMembers(code, accessToken, version),
            LOW_PRIORITY
        )
    }

    override fun getUser(fields: String): Single<GetUserResponse> {
        return getSingleWithOfferToQueue(apiClient.getUser(fields), HIGH_PRIORITY)
    }

    private fun <T> getSingleWithOfferToQueue(single: Single<T>, priority: Int): Single<T> {
        val prioritySingle: PrioritySingle<T> = PrioritySingle(single, priority)
        return Completable.fromAction { queue.put(prioritySingle) }
            .andThen(prioritySingle.single)
            .doAfterTerminate { queue.remove(prioritySingle) }
    }

    private class PrioritySingle<T>(val single: Single<T>, val priority: Int)

    companion object {
        private const val QUEUE_CAPACITY = 3
        private const val HIGH_PRIORITY = 0
        private const val LOW_PRIORITY = 1
    }
}