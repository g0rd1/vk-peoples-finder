package ru.g0rd1.peoplesfinder.ui.lists

import androidx.compose.runtime.Immutable
import ru.g0rd1.peoplesfinder.model.User
import ru.g0rd1.peoplesfinder.model.UserType

@Immutable
sealed class ListsViewData {
    @Immutable
    object Loading : ListsViewData()

    @Immutable
    data class Data(
        val userType: UserType,
        val users: List<User>,
    ) : ListsViewData()
}