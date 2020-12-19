package ru.g0rd1.peoplesfinder.mapper

import ru.g0rd1.peoplesfinder.apiservice.model.ApiUser
import ru.g0rd1.peoplesfinder.db.entity.UserEntity
import ru.g0rd1.peoplesfinder.model.User
import javax.inject.Inject

class UserMapper @Inject constructor() {

    fun transform(apiUser: ApiUser): User {
        return User(
            id = apiUser.id,
            firstName = apiUser.firstName,
            lastName = apiUser.lastName,
            deactivated = apiUser.deactivated,
            isClosed = apiUser.isClosed,
            birthDate = apiUser.birthDate,
            city = apiUser.city,
            sex = apiUser.sex,
            photo = apiUser.photo,
            lastSeen = apiUser.lastSeen,
            relation = apiUser.relation
        )
    }

    fun transform(userEntity: UserEntity): User {
        return User(
            id = userEntity.id,
            firstName = userEntity.firstName,
            lastName = userEntity.lastName,
            deactivated = userEntity.deactivated,
            isClosed = userEntity.isClosed,
            birthDate = userEntity.birthDate,
            city = userEntity.city,
            sex = userEntity.sex,
            photo = userEntity.photo,
            lastSeen = userEntity.lastSeen,
            relation = userEntity.relation
        )
    }

    fun transformToEntity(user: User): UserEntity {
        return UserEntity(
            id = user.id,
            firstName = user.firstName,
            lastName = user.lastName,
            deactivated = user.deactivated,
            isClosed = user.isClosed,
            birthDate = user.birthDate,
            city = user.city,
            sex = user.sex,
            photo = user.photo,
            lastSeen = user.lastSeen,
            relation = user.relation
        )
    }

}