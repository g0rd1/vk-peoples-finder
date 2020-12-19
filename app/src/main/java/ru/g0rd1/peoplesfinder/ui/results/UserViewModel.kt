package ru.g0rd1.peoplesfinder.ui.results

import ru.g0rd1.peoplesfinder.model.User

data class UserViewModel(
    val user: User,
    val sameGroupCount: Int
)
