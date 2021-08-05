package ru.g0rd1.peoplesfinder.db.query

import androidx.room.ColumnInfo
import androidx.room.Embedded
import ru.g0rd1.peoplesfinder.db.entity.UserEntity

class UserEntityWithHistoryId (
    @Embedded
    val userEntity: UserEntity,
    @ColumnInfo(name = HISTORY_ID_COLUMN_NAME)
    val historyId: Int
) {
    companion object {
        const val HISTORY_ID_COLUMN_NAME = "history_id"
    }
}