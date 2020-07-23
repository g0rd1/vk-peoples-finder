package ru.g0rd1.peoplesfinder.apiservice

import io.reactivex.Completable
import io.reactivex.Single
import ru.g0rd1.peoplesfinder.apiservice.response.GetGroupMembersResponse
import ru.g0rd1.peoplesfinder.apiservice.response.GetGroupsResponse
import ru.g0rd1.peoplesfinder.model.Group
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue

class QueueApiClient(apiClient: ApiClient) : ApiClientDecorator(apiClient) {

    private val queue: BlockingQueue<Any> = ArrayBlockingQueue(QUEUE_CAPACITY)

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
            )
        )
    }

    override fun getGroupMembers(
        code: String,
        accessToken: String,
        version: String
    ): Single<GetGroupMembersResponse> {
        return getSingleWithOfferToQueue(apiClient.getGroupMembers(code, accessToken, version))
    }

    override fun getGroupsByQuery(
        query: String,
        count: Int,
        accessToken: String,
        version: String
    ): Single<List<Group>> {
        return getSingleWithOfferToQueue(
            apiClient.getGroupsByQuery(
                query,
                count,
                accessToken,
                version
            )
        )
    }

    private fun <T> getSingleWithOfferToQueue(single: Single<T>): Single<T> {
        val singleWithActionsAfterTerminate = single.map {
            queue.remove(single)
            it
        }
            .doOnError { queue.remove(single) }
        return Completable.fromAction { queue.put(single) }.andThen(singleWithActionsAfterTerminate)
    }

    companion object {
        private const val QUEUE_CAPACITY = 3
    }
}