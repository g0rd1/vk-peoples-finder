package ru.g0rd1.peoplesfinder.mapper

import ru.g0rd1.peoplesfinder.apiservice.model.ApiUser
import ru.g0rd1.peoplesfinder.db.entity.UserEntity
import ru.g0rd1.peoplesfinder.db.entity.UserTypeEntity
import ru.g0rd1.peoplesfinder.model.User
import ru.g0rd1.peoplesfinder.model.UserType
import java.util.*
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
            birthday = apiUser.birthday,
            age = birthdayToAge(apiUser.birthday),
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
            birthday = userEntity.birthday,
            age = userEntity.age,
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
            age = user.age,
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

    private fun birthdayToAge(birthday: String?): Int? {
        birthday ?: return null
        val birthdayParts = birthday.split(".")
        if (birthdayParts.size != 3) return null
        return getAge(
            year = birthdayParts[2].toInt(),
            month = birthdayParts[1].toInt(),
            day = birthdayParts[0].toInt()
        )
    }

    private fun getAge(year: Int, month: Int, day: Int): Int {
        val birthday = Calendar.getInstance()
        val today = Calendar.getInstance()
        birthday.set(year, month, day)
        return if (today[Calendar.YEAR] <= birthday[Calendar.YEAR]) {
            0
        } else if (today[Calendar.MONTH] < birthday[Calendar.MONTH]) {
            today[Calendar.YEAR] - birthday[Calendar.YEAR] - 1
        } else if (today[Calendar.MONTH] == birthday[Calendar.MONTH]) {
            if (today[Calendar.DAY_OF_MONTH] < birthday[Calendar.DAY_OF_MONTH]) {
                today[Calendar.YEAR] - birthday[Calendar.YEAR] - 1
            } else {
                today[Calendar.YEAR] - birthday[Calendar.YEAR]
            }
        } else {
            today[Calendar.YEAR] - birthday[Calendar.YEAR]
        }
    }

}

