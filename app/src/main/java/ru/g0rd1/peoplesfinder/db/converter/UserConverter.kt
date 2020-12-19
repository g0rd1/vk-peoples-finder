package ru.g0rd1.peoplesfinder.db.converter

import androidx.room.TypeConverter
import ru.g0rd1.peoplesfinder.common.UserLastSeen
import ru.g0rd1.peoplesfinder.common.UserRelation
import ru.g0rd1.peoplesfinder.common.UserSex

class UserConverter {

    @TypeConverter
    fun fromPlatform(platform: UserLastSeen.Platform?): String? {
        return platform?.toString()
    }

    @TypeConverter
    fun toPlatform(value: String?): UserLastSeen.Platform? {
        return value?.let { UserLastSeen.Platform.valueOf(it) }
    }

    @TypeConverter
    fun fromSex(sex: UserSex?): String? {
        return sex?.toString()
    }

    @TypeConverter
    fun toSex(value: String?): UserSex? {
        return value?.let { UserSex.valueOf(it) }
    }

    @TypeConverter
    fun fromRelation(relation: UserRelation?): String? {
        return relation?.toString()
    }

    @TypeConverter
    fun toRelation(value: String?): UserRelation? {
        return value?.let { UserRelation.valueOf(it) }
    }

}