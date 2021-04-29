package ru.g0rd1.peoplesfinder.db.entity

import androidx.room.*

@Entity(
    tableName = GroupHistoryEntity.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = GroupEntity::class,
            parentColumns = arrayOf(GroupEntity.Column.ID),
            childColumns = arrayOf(GroupHistoryEntity.Column.GROUP_ID),
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = [GroupHistoryEntity.Column.GROUP_ID], unique = true)]
)
data class GroupHistoryEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Column.ID)
    val id: Int = 0,

    @ColumnInfo(name = Column.GROUP_ID)
    val groupId: Int,
) {

    object Column {
        const val ID = "id"
        const val GROUP_ID = "group_id"
    }

    companion object {
        const val TABLE_NAME = "group_histories"
    }

}