package ru.g0rd1.peoplesfinder.model.result

import ru.g0rd1.peoplesfinder.model.Group
import ru.g0rd1.peoplesfinder.model.User
import ru.g0rd1.peoplesfinder.model.UserType

sealed class UserWithSameGroupsAndUserTypesResult {
    object Empty : UserWithSameGroupsAndUserTypesResult()
    data class Success(
        val user: User,
        val sameGroups: List<Group>,
        val userTypes: List<UserType>,
    ) : UserWithSameGroupsAndUserTypesResult()
}