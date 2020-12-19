package ru.g0rd1.peoplesfinder.db.query

import androidx.room.ColumnInfo
import androidx.room.Embedded
import ru.g0rd1.peoplesfinder.db.entity.UserEntity

data class UserEntityWithSameGroupsCountQuery(
    @Embedded val userEntity: UserEntity,
    @ColumnInfo(name = SAME_GROUPS_COUNT_COLUMN_NAME) val sameGroupsCount: Int
) {
    companion object {
        const val SAME_GROUPS_COUNT_COLUMN_NAME = "same_groups_count"
    }
}