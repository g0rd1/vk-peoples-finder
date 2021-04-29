package ru.g0rd1.peoplesfinder.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import ru.g0rd1.peoplesfinder.db.entity.UserGroupEntity.Column
import ru.g0rd1.peoplesfinder.db.entity.UserGroupEntity.Companion.TABLE_NAME

@Entity(
    tableName = TABLE_NAME,
    primaryKeys = [Column.USER_ID, Column.GROUP_ID],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = [UserEntity.Column.ID],
            childColumns = [Column.USER_ID],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = GroupEntity::class,
            parentColumns = [GroupEntity.Column.ID],
            childColumns = [Column.GROUP_ID],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserGroupEntity(
    @ColumnInfo(name = Column.USER_ID)
    val userId: Int,

    @ColumnInfo(name = Column.GROUP_ID)
    val groupId: Int,
) {

    object Column {
        const val USER_ID = "user_id"
        const val GROUP_ID = "group_id"
    }

    companion object {
        const val TABLE_NAME = "user_group"
    }
}