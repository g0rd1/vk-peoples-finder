package ru.g0rd1.peoplesfinder.db.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.g0rd1.peoplesfinder.common.enums.Relation
import ru.g0rd1.peoplesfinder.common.enums.Sex
import ru.g0rd1.peoplesfinder.db.entity.UserEntity.Column.BIRTHDAY_EPOCH_DAYS
import ru.g0rd1.peoplesfinder.db.entity.UserEntity.Column.CITY_PREFIX
import ru.g0rd1.peoplesfinder.db.entity.UserEntity.Column.COUNTRY_PREFIX
import ru.g0rd1.peoplesfinder.db.entity.UserEntity.Column.HAS_PHOTO
import ru.g0rd1.peoplesfinder.db.entity.UserEntity.Column.IS_CLOSED
import ru.g0rd1.peoplesfinder.db.entity.UserEntity.Column.RELATION
import ru.g0rd1.peoplesfinder.db.entity.UserEntity.Column.SEX
import ru.g0rd1.peoplesfinder.db.entity.UserEntity.Companion.TABLE_NAME
import ru.g0rd1.peoplesfinder.model.City
import ru.g0rd1.peoplesfinder.model.Country
import java.time.LocalDate

@Entity(tableName = TABLE_NAME)
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

    @ColumnInfo(name = IS_CLOSED)
    val isClosed: Boolean,

    @ColumnInfo(name = BIRTHDAY_EPOCH_DAYS)
    val birthday: LocalDate?,

    @Embedded(prefix = COUNTRY_PREFIX)
    val country: Country?,

    @Embedded(prefix = CITY_PREFIX)
    val city: City?,

    @ColumnInfo(name = SEX)
    val sex: Sex?,

    @ColumnInfo(name = RELATION)
    val relation: Relation?,

    @ColumnInfo(name = HAS_PHOTO)
    val hasPhoto: Boolean?,

    @ColumnInfo(name = "photo_100")
    val photo100: String?,

    @ColumnInfo(name = "photo_max")
    val photoMax: String?
) {

    object Column {
        const val ID = "id"
        const val BIRTHDAY_EPOCH_DAYS = "bdate_epoch_days"
        const val COUNTRY_PREFIX = "country_"
        const val CITY_PREFIX = "city_"
        const val SEX = "sex"
        const val RELATION = "relation"
        const val HAS_PHOTO = "has_photo"
        const val IS_CLOSED = "is_closed"
    }

    companion object {
        const val TABLE_NAME = "user"
    }

}