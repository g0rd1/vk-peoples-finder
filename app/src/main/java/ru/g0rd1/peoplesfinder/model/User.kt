package ru.g0rd1.peoplesfinder.model

import ru.g0rd1.peoplesfinder.common.UserCity
import ru.g0rd1.peoplesfinder.common.UserLastSeen
import ru.g0rd1.peoplesfinder.common.UserRelation
import ru.g0rd1.peoplesfinder.common.UserSex

data class User(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val deactivated: String?,
    val isClosed: Boolean,
    val birthDate: String?,
    val city: UserCity?,
    val sex: UserSex?,
    val photo: String?,
    val lastSeen: UserLastSeen?,
    val relation: UserRelation?
)