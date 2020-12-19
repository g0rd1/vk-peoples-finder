package ru.g0rd1.peoplesfinder.db.entity

import androidx.room.*
import ru.g0rd1.peoplesfinder.db.converter.DateConverter
import java.util.*


@Entity(
    tableName = "group_data",
    foreignKeys = [
        ForeignKey(
            onDelete = ForeignKey.CASCADE,
            entity = GroupEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("groupId")
        )
    ]
)
@TypeConverters(DateConverter::class)
data class GroupDataEntity(

    @PrimaryKey
    val groupId: Int,

    @ColumnInfo(name = "loaded_members_count")
    val loadedMembersCount: Int,

    @ColumnInfo(name = "all_members_loaded_date")
    val allMembersLoadedDate: Date?,

    @ColumnInfo(name = "sequential_number")
    val sequentialNumber: Int,

    @ColumnInfo(name = "has_access_to_members")
    val hasAccessToMembers: Boolean
)