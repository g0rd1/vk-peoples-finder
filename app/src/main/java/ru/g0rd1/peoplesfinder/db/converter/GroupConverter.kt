package ru.g0rd1.peoplesfinder.db.converter

import androidx.room.TypeConverter
import ru.g0rd1.peoplesfinder.common.enums.GroupType

class GroupConverter {

    @TypeConverter
    fun fromGroupType(type: GroupType): String {
        return type.name
    }

    @TypeConverter
    fun toGroupType(value: String): GroupType {
        return GroupType.valueOf(value)
    }

}