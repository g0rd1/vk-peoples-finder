package ru.g0rd1.peoplesfinder.ui.lists

import ru.g0rd1.peoplesfinder.model.User
import ru.g0rd1.peoplesfinder.model.UserType

sealed class ListsViewData {
    object Loading : ListsViewData()
    data class Data(
        val userType: UserType,
        val users: List<User>,
    ) : ListsViewData()
}