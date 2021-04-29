package ru.g0rd1.peoplesfinder.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.g0rd1.peoplesfinder.common.enums.GroupType
import ru.g0rd1.peoplesfinder.db.entity.GroupEntity.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class GroupEntity constructor(

    @PrimaryKey
    @ColumnInfo(name = Column.ID)
    val id: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "deactivated")
    val deactivated: String?,

    @ColumnInfo(name = "photo_200")
    val photo: String?,

    @ColumnInfo(name = "type")
    val type: GroupType,

    @ColumnInfo(name = "members_count")
    val membersCount: Int

) {

    object Column {
        const val ID = "id"
    }

    companion object {
        const val TABLE_NAME = "groups"
    }
}