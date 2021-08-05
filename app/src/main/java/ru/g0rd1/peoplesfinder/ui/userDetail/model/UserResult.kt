package ru.g0rd1.peoplesfinder.ui.userDetail.model

import ru.g0rd1.peoplesfinder.model.User

sealed class UserResult(open val user: User) : Comparable<UserResult> {
    data class FromSearch(override val user: User) : UserResult(user) {
        override fun compareTo(other: UserResult): Int {
            return if (other is FromHistory) {
                1
            } else {
                this.user.id.compareTo(other.user.id)
            }
        }
    }

    data class FromHistory(override val user: User, val historyId: Int) : UserResult(user) {
        override fun compareTo(other: UserResult): Int {
            return if (other is FromHistory) {
                this.historyId.compareTo(other.historyId)
            } else {
                -1
            }
        }
    }
}
