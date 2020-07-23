package ru.g0rd1.peoplesfinder.repo.user.local

import io.reactivex.Completable
import io.reactivex.Single
import ru.g0rd1.peoplesfinder.db.entity.UserEntity

interface LocalUsersRepo {

    fun insert(userEntity: UserEntity): Completable

    fun insert(userEntities: List<UserEntity>): Completable

    fun delete(userEntity: UserEntity): Completable

    fun getUsers(): Single<List<UserEntity>>

    fun getUsersWithGroups(): Single<List<UserEntity>>

    fun insertWithGroups(userEntity: UserEntity, groupIds: List<Int>): Completable

    fun insertWithGroups(userEntitiesWithGroupIds: Map<UserEntity, List<Int>>): Completable

    companion object {
        const val QUEUE = "QueueLocalUsersRepo"
    }

}