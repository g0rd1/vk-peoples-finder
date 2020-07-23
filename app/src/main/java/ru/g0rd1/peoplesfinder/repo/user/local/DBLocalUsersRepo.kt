package ru.g0rd1.peoplesfinder.repo.user.local

import io.reactivex.Completable
import io.reactivex.Single
import ru.g0rd1.peoplesfinder.db.dao.UserDao
import ru.g0rd1.peoplesfinder.db.entity.UserEntity
import javax.inject.Inject

class DBLocalUsersRepo @Inject constructor(
    private val userDao: UserDao
) : LocalUsersRepo {

    override fun insert(userEntity: UserEntity): Completable =
        Completable.fromAction { userDao.insertOrUpdate(userEntity) }

    override fun insert(userEntities: List<UserEntity>): Completable =
        Completable.fromAction { userDao.insertOrUpdate(userEntities) }

    override fun delete(userEntity: UserEntity): Completable = userDao.delete(userEntity)

    override fun getUsers(): Single<List<UserEntity>> = userDao.get()

    override fun getUsersWithGroups(): Single<List<UserEntity>> =
        Single.just(userDao.getWithGroups())

    override fun insertWithGroups(userEntity: UserEntity, groupIds: List<Int>): Completable =
        Completable.fromAction { userDao.insertOrUpdateWithGroups(userEntity, groupIds) }

    override fun insertWithGroups(userEntitiesWithGroupIds: Map<UserEntity, List<Int>>): Completable =
        Completable.fromAction { userDao.insertOrUpdateWithGroups(userEntitiesWithGroupIds) }
}