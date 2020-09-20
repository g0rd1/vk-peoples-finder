package ru.g0rd1.peoplesfinder.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import ru.g0rd1.peoplesfinder.db.entity.GroupEntity.Companion.TABLE_NAME
import ru.g0rd1.peoplesfinder.model.Group
import java.util.*

@Entity(tableName = TABLE_NAME)
data class GroupEntity(

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
    val type: String,

    @ColumnInfo(name = "members_count")
    val membersCount: Int,

    @ColumnInfo(name = "loaded_members_count")
    val loadedMembersCount: Int,

    @ColumnInfo(name = "all_members_loaded_date")
    val allMembersLoadedDate: Date?

) {

    @Ignore
    var users: List<UserEntity>? = null

    fun isAllMembersLoaded(): Boolean = (membersCount == loadedMembersCount)

    constructor(
        group: Group,
        loadedMembersCount: Int = 0,
        allMembersLoadedDate: Date? = null
    ) : this(
        group.id,
        group.name,
        group.deactivated,
        group.photo,
        group.type.value,
        group.membersCount,
        loadedMembersCount,
        allMembersLoadedDate
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GroupEntity) return false
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
        const val TABLE_NAME = "group"
    }
}