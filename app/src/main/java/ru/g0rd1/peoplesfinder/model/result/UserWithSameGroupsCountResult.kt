package ru.g0rd1.peoplesfinder.model.result

import ru.g0rd1.peoplesfinder.model.User

sealed class UserWithSameGroupsCountResult {
    object Empty : UserWithSameGroupsCountResult()
    data class Result(
        val user: User,
        val sameGroupsCount: Int
    ) : UserWithSameGroupsCountResult()
}