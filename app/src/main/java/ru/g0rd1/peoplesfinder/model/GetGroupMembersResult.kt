package ru.g0rd1.peoplesfinder.model

import ru.g0rd1.peoplesfinder.model.error.VkError

sealed class GetGroupMembersResult {
    data class Success(val users: List<User>) : GetGroupMembersResult()
    sealed class Error : GetGroupMembersResult() {
        data class Vk(val error: VkError) : Error()
        data class Generic(val throwable: Throwable) : Error()
    }
}