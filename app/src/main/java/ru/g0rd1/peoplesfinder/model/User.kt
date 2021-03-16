package ru.g0rd1.peoplesfinder.model

import ru.g0rd1.peoplesfinder.common.enums.Relation
import ru.g0rd1.peoplesfinder.common.enums.Sex

data class User(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val deactivated: String?,
    val isClosed: Boolean,
    val birthday: String?,
    val age: Int?,
    val city: City?,
    val country: Country?,
    val sex: Sex?,
    val relation: Relation?,
    val hasPhoto: Boolean?,
    val photo100: String?,
    val photoMax: String?
)