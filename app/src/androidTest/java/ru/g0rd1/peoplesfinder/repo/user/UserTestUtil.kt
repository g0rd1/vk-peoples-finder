package ru.g0rd1.peoplesfinder.repo.user

import ru.g0rd1.peoplesfinder.db.entity.UserEntity
import ru.g0rd1.peoplesfinder.model.User

object UserTestUtil {

    fun getTestUserEntity(
        id: Int = 0,
        firstName: String = "",
        lastName: String = "",
        deactivated: String? = null,
        isClosed: Boolean = false,
        birthDate: String? = null,
        city: User.City? = null,
        sex: Int? = null,
        photo: String? = null,
        lastSeen: User.LastSeen? = null,
        relation: Int? = null
    ): UserEntity {
        return UserEntity(
            user = User(
                id,
                firstName,
                lastName,
                deactivated,
                isClosed,
                birthDate,
                city,
                sex,
                photo,
                lastSeen,
                relation
            )
        )
    }

    fun getTestUser(
        id: Int = 0,
        firstName: String = "",
        lastName: String = "",
        deactivated: String? = null,
        isClosed: Boolean = false,
        birthDate: String? = null,
        city: User.City? = null,
        sex: Int? = null,
        photo: String? = null,
        lastSeen: User.LastSeen? = null,
        relation: Int? = null
    ): User {
        return User(
            id,
            firstName,
            lastName,
            deactivated,
            isClosed,
            birthDate,
            city,
            sex,
            photo,
            lastSeen,
            relation
        )
    }
}