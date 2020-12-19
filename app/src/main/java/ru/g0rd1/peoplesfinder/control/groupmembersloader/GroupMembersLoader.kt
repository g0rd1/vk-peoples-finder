package ru.g0rd1.peoplesfinder.control.groupmembersloader

import io.reactivex.Single
import ru.g0rd1.peoplesfinder.model.error.VkError

interface GroupMembersLoader {

    fun load(): Single<LoadResult>

    interface Factory {
        fun create(groupId: Int): GroupMembersLoader
    }

    sealed class LoadResult {
        object Success : LoadResult()
        sealed class Error : LoadResult() {
            data class Vk(val error: VkError) : Error()
            data class Generic(val throwable: Throwable) : Error()
        }
    }

}