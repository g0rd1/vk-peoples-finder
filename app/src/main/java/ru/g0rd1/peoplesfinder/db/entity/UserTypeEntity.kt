package ru.g0rd1.peoplesfinder.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.g0rd1.peoplesfinder.db.entity.UserTypeEntity.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
class UserTypeEntity(
    @PrimaryKey
    @ColumnInfo(name = Column.ID)
    val id: Int,

    @ColumnInfo(name = "name")
    val name: String
) {

    object Column {
        const val ID = "id"
    }

    companion object {
        const val TABLE_NAME = "user_type"
    }

}