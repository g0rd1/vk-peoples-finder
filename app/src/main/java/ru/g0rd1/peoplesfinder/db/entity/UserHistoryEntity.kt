package ru.g0rd1.peoplesfinder.db.entity

import androidx.room.*

@Entity(
    tableName = UserHistoryEntity.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = arrayOf(UserEntity.Column.ID),
            childColumns = arrayOf(UserHistoryEntity.Column.USER_ID),
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = [UserHistoryEntity.Column.USER_ID], unique = true)]
)
data class UserHistoryEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Column.ID)
    val id: Int = 0,

    @ColumnInfo(name = Column.USER_ID)
    val userId: Int,
) {

    object Column {
        const val ID = "id"
        const val USER_ID = "user_id"
    }

    companion object {
        const val TABLE_NAME = "user_histories"
    }

}