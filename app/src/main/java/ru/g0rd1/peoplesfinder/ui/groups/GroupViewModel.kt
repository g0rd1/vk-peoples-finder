package ru.g0rd1.peoplesfinder.ui.groups

data class GroupViewModel(
    val id: Int,
    val name: String,
    val photo: String?,
    val membersCount: Int,
    val loadedMembersCount: Int,
    val hasAccessToMembers: Boolean
)