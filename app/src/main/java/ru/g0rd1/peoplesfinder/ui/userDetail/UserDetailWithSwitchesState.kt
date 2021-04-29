package ru.g0rd1.peoplesfinder.ui.userDetail

import ru.g0rd1.peoplesfinder.model.Optional

data class UserDetailWithSwitchesState(
    val previousUserId: Optional<String>,
    val currentUserId: Optional<String>,
    val nextUserId: Optional<String>
)
