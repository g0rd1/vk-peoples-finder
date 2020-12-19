package ru.g0rd1.peoplesfinder.db.entity

import androidx.room.*
import ru.g0rd1.peoplesfinder.common.UserCity
import ru.g0rd1.peoplesfinder.common.UserLastSeen
import ru.g0rd1.peoplesfinder.common.UserRelation
import ru.g0rd1.peoplesfinder.common.UserSex
import ru.g0rd1.peoplesfinder.db.converter.UserConverter
import ru.g0rd1.peoplesfinder.db.entity.UserEntity.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
@TypeConverters(UserConverter::class)
data class UserEntity(

    @PrimaryKey
    @ColumnInfo(name = Column.ID)
    val id: Int,

    @ColumnInfo(name = "first_name")
    val firstName: String,

    @ColumnInfo(name = "last_name")
    val lastName: String,

    @ColumnInfo(name = "deactivated")
    val deactivated: String?,

    @ColumnInfo(name = "is_closed")
    val isClosed: Boolean,

    @ColumnInfo(name = "bdate")
    val birthDate: String?,

    @Embedded(prefix = "city_")
    val city: UserCity?,

    @ColumnInfo(name = "sex")
    val sex: UserSex?,

    @ColumnInfo(name = "photo_200")
    val photo: String?,

    @Embedded(prefix = "last_seen_")
    val lastSeen: UserLastSeen?,

    @ColumnInfo(name = "relation")
    val relation: UserRelation?
) {

    object Column {
        const val ID = "id"
    }

    companion object {
        const val TABLE_NAME = "user"
    }

}