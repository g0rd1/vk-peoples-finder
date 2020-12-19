package ru.g0rd1.peoplesfinder.db.converter

import androidx.room.TypeConverter
import ru.g0rd1.peoplesfinder.common.GroupType

class GroupConverter {

    @TypeConverter
    fun fromType(type: GroupType): String {
        return type.toString()
    }

    @TypeConverter
    fun toType(value: String): GroupType {
        return GroupType.valueOf(value)
    }

}