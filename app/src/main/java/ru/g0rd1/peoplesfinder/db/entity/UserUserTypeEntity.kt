package ru.g0rd1.peoplesfinder.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import ru.g0rd1.peoplesfinder.db.entity.UserUserTypeEntity.Column
import ru.g0rd1.peoplesfinder.db.entity.UserUserTypeEntity.Companion.TABLE_NAME

@Entity(
    tableName = TABLE_NAME,
    primaryKeys = [Column.USER_ID, Column.USER_TYPE_ID]
)
class UserUserTypeEntity(
    @ColumnInfo(name = Column.USER_ID)
    val userId: Int,

    @ColumnInfo(name = Column.USER_TYPE_ID)
    val userTypeId: Int
) {
    object Column {
        const val USER_ID = "user_id"
        const val USER_TYPE_ID = "user_type_id"
    }

    companion object {
        const val TABLE_NAME = "user_user_type"
    }
}