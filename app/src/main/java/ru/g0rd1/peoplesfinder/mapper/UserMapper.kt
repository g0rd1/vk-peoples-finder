package ru.g0rd1.peoplesfinder.mapper

import ru.g0rd1.peoplesfinder.apiservice.model.ApiUser
import ru.g0rd1.peoplesfinder.db.entity.UserEntity
import ru.g0rd1.peoplesfinder.db.entity.UserTypeEntity
import ru.g0rd1.peoplesfinder.model.User
import ru.g0rd1.peoplesfinder.model.UserType
import java.time.LocalDate
import javax.inject.Inject

class UserMapper @Inject constructor(
    private val cityMapper: CityMapper,
    private val countryMapper: CountryMapper
) {

    fun transform(apiUser: ApiUser): User {
        return User(
            id = apiUser.id,
            firstName = apiUser.firstName,
            lastName = apiUser.lastName,
            deactivated = apiUser.deactivated,
            isClosed = apiUser.isClosed,
            birthday = birthdayRawToLocalDate(apiUser.birthdayRaw),
            country = apiUser.country?.let { countryMapper.transform(it) },
            city = apiUser.city?.let { cityMapper.transform(it) },
            sex = apiUser.sex,
            hasPhoto = apiUser.hasPhoto == 1,
            relation = apiUser.relation,
            photo100 = apiUser.photo100,
            photoMax = apiUser.photoMax
        )
    }

    fun transform(userEntity: UserEntity): User {
        return User(
            id = userEntity.id,
            firstName = userEntity.firstName,
            lastName = userEntity.lastName,
            deactivated = userEntity.deactivated,
            isClosed = userEntity.isClosed,
            birthday= userEntity.birthday,
            country = userEntity.country,
            city = userEntity.city,
            sex = userEntity.sex,
            relation = userEntity.relation,
            hasPhoto = userEntity.hasPhoto,
            photo100 = userEntity.photo100,
            photoMax = userEntity.photoMax
        )
    }

    fun transformToEntity(user: User): UserEntity {
        return UserEntity(
            id = user.id,
            firstName = user.firstName,
            lastName = user.lastName,
            deactivated = user.deactivated,
            isClosed = user.isClosed,
            birthday = user.birthday,
            country = user.country,
            city = user.city,
            sex = user.sex,
            relation = user.relation,
            hasPhoto = user.hasPhoto,
            photo100 = user.photo100,
            photoMax = user.photoMax
        )
    }

    fun transform(userTypeEntity: UserTypeEntity): UserType {
        return UserType(
            id = userTypeEntity.id,
            name = userTypeEntity.name
        )
    }

    private fun birthdayRawToLocalDate(birthdayRaw: String?): LocalDate? {
        birthdayRaw ?: return null
        val birthdayParts = birthdayRaw.split(".")
        if (birthdayParts.size != 3) return null
        if (birthdayParts.any { it.isBlank() }) return null
        val year = birthdayParts[2].toLong()
        val month = birthdayParts[1].toLong()
        val dayOfMonth = birthdayParts[0].toLong()
        val epochDate: LocalDate = LocalDate.ofEpochDay(0)
        return epochDate
            .plusYears(year - epochDate.year)
            .plusMonths(month - epochDate.monthValue)
            .plusDays(dayOfMonth - epochDate.dayOfMonth)
    }

}

