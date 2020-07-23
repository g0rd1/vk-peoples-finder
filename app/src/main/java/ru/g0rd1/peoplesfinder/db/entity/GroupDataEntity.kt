package ru.g0rd1.peoplesfinder.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "group_data")
data class GroupDataEntity(

    @PrimaryKey
    val groupId: Long,

    @ColumnInfo(name = "use_in_search")
    val useInSearch: Boolean,

    @ColumnInfo(name = "members_loaded")
    val membersLoaded: Boolean
)