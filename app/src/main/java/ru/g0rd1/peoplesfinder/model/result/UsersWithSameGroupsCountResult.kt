package ru.g0rd1.peoplesfinder.model.result

import ru.g0rd1.peoplesfinder.model.User

sealed class UsersWithSameGroupsCountResult {
    object Empty : UsersWithSameGroupsCountResult()
    data class Result(
        val data: Map<User, Int>
    ) : UsersWithSameGroupsCountResult()
}