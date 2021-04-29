package ru.g0rd1.peoplesfinder.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ru.g0rd1.peoplesfinder.db.entity.GroupInfoEntity.Companion.TABLE_NAME
import java.time.LocalDate


@Entity(
    tableName = TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            onDelete = ForeignKey.CASCADE,
            entity = GroupEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("groupId")
        )
    ]
)
data class GroupInfoEntity(

    @PrimaryKey
    val groupId: Int,

    @ColumnInfo(name = "loaded_members_count")
    val loadedMembersCount: Int,

    @ColumnInfo(name = "all_members_loaded_date")
    val allMembersLoadedDate: LocalDate?,

    @ColumnInfo(name = "sequential_number")
    val sequentialNumber: Int,

    @ColumnInfo(name = "has_access_to_members")
    val hasAccessToMembers: Boolean,
) {

    companion object {
        const val TABLE_NAME = "group_info"
    }

}