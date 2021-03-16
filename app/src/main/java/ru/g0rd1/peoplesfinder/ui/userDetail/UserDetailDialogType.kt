package ru.g0rd1.peoplesfinder.ui.userDetail

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class UserDetailDialogType: Parcelable {
    @Parcelize data class Single(val userId: Int) : UserDetailDialogType()
    @Parcelize object WithSwitches : UserDetailDialogType()
}