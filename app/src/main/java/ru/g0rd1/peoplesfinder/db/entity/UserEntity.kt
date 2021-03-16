package ru.g0rd1.peoplesfinder.db.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.g0rd1.peoplesfinder.common.enums.Relation
import ru.g0rd1.peoplesfinder.common.enums.Sex
import ru.g0rd1.peoplesfinder.db.entity.UserEntity.Companion.TABLE_NAME
import ru.g0rd1.peoplesfinder.model.City
import ru.g0rd1.peoplesfinder.model.Country

@Entity(tableName = TABLE_NAME)
//@TypeConverters(UserConverter::class)
data class UserEntity(

    @PrimaryKey
    @ColumnInfo(name = Column.ID)
    var id: Int,

    @ColumnInfo(name = "first_name")
    var firstName: String,

    @ColumnInfo(name = "last_name")
    var lastName: String,

    @ColumnInfo(name = "deactivated")
    var deactivated: String?,

    @ColumnInfo(name = "is_closed")
    var isClosed: Boolean,

    @ColumnInfo(name = "bdate")
    var birthday: String?,

    @ColumnInfo(name = "age")
    var age: Int?,

    @Embedded(prefix = "country_")
    var country: Country?,

    @Embedded(prefix = "city_")
    var city: City?,

    @ColumnInfo(name = "sex")
    var sex: Sex?,

    @ColumnInfo(name = "relation")
    var relation: Relation?,

    @ColumnInfo(name = "has_photo")
    var hasPhoto: Boolean?,

    @ColumnInfo(name = "photo_100")
    var photo100: String?,

    @ColumnInfo(name = "photo_max")
    var photoMax: String?
) {

    object Column {
        const val ID = "id"
    }

    companion object {
        const val TABLE_NAME = "user"
    }

}