package ru.g0rd1.peoplesfinder.db.converter

import androidx.room.TypeConverter
import ru.g0rd1.peoplesfinder.common.enums.Relation
import ru.g0rd1.peoplesfinder.common.enums.Sex

class UserConverter {

    @TypeConverter
    fun fromSex(sex: Sex?): String? {
        return sex?.name
    }

    @TypeConverter
    fun toSex(value: String?): Sex? {
        return value?.let { Sex.valueOf(it) }
    }

    @TypeConverter
    fun fromRelation(relation: Relation?): String? {
        return relation?.name
    }

    @TypeConverter
    fun toRelation(value: String?): Relation? {
        return value?.let { Relation.valueOf(it) }
    }

}