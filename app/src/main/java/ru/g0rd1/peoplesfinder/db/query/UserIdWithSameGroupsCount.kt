package ru.g0rd1.peoplesfinder.db.query

import androidx.room.ColumnInfo

data class UserIdWithSameGroupsCount(
    @ColumnInfo(name = USER_ID) val userId: Int,
    @ColumnInfo(name = SAME_GROUPS_COUNT_COLUMN_NAME) val sameGroupsCount: Int,
) {
    companion object {
        const val USER_ID = "user_id"
        const val SAME_GROUPS_COUNT_COLUMN_NAME = "same_groups_count"
    }
}