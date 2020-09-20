package ru.g0rd1.peoplesfinder.db.entity

import androidx.room.*
import ru.g0rd1.peoplesfinder.db.entity.UserEntity.Companion.TABLE_NAME
import ru.g0rd1.peoplesfinder.model.User

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

    @ColumnInfo(name = "is_closed")
    val isClosed: Boolean,

    @ColumnInfo(name = "bdate")
    val birthDate: String?,

    @Embedded(prefix = "city")
    val city: User.City?,

    @ColumnInfo(name = "sex")
    val sex: Int?,

    @ColumnInfo(name = "photo_200")
    val photo: String?,

    @Embedded(prefix = "last_seen")
    val lastSeen: User.LastSeen?,

    @ColumnInfo(name = "relation")
    val relation: Int?
) {

    @Ignore
    var groups: List<GroupEntity>? = null

    @Ignore
    var sameGroupsCount: Int? = null

    constructor(user: User) : this(
        id = user.userId,
        firstName = user.firstName,
        lastName = user.lastName,
        deactivated = user.deactivated,
        isClosed = user.isClosed,
        birthDate = user.birthDate,
        city = user.city,
        sex = user.sex,
        photo = user.photo,
        lastSeen = user.lastSeen?.let { User.LastSeen(it.time, it.platform) },
        relation = user.relation
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserEntity) return false
        if (this.id == other.id) return true
        return false
    }

    override fun hashCode(): Int {
        return id
    }

    object Column {
        const val ID = "id"
    }

    companion object {
        const val TABLE_NAME = "user"
    }

}