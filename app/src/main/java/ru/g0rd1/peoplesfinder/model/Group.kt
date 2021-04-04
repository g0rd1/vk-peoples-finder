package ru.g0rd1.peoplesfinder.model

import ru.g0rd1.peoplesfinder.common.enums.GroupType
import java.time.LocalDate

data class Group(
    val id: Int,
    val name: String,
    val deactivated: String?,
    val photo: String?,
    val type: GroupType,
    val membersCount: Int,
    val loadedMembersCount: Int,
    val allMembersLoadedDate: LocalDate?,
    val sequentialNumber: Int,
    val hasAccessToMembers: Boolean
)