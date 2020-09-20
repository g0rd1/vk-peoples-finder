package ru.g0rd1.peoplesfinder.repo.user.local

import io.reactivex.Completable
import io.reactivex.Single
import ru.g0rd1.peoplesfinder.db.dao.UserDao
import ru.g0rd1.peoplesfinder.db.entity.UserEntity
import timber.log.Timber
import javax.inject.Inject

class DBLocalUsersRepo @Inject constructor(
    private val userDao: UserDao
) : LocalUsersRepo {

    override fun insert(userEntity: UserEntity): Completable =
        userDao.insert(userEntity)

    override fun insert(userEntities: List<UserEntity>): Completable =
        userDao.insert(userEntities)

    override fun delete(userEntity: UserEntity): Completable = userDao.delete(userEntity)

    override fun get(): Single<List<UserEntity>> = userDao.get()

    override fun getWithSameGroupsCount(): Single<List<UserEntity>> =
        userDao.getWithSameGroupsCount().map { userEntitiesWithSameGroupsCounts ->
            userEntitiesWithSameGroupsCounts.map {
                it.user.apply {
                    sameGroupsCount = it.sameGroupsCount
                }
            }
        }

    override fun getWithGroups(): Single<List<UserEntity>> =
        Single.fromCallable { userDao.getWithGroups() }

    override fun insertWithGroups(userEntity: UserEntity, groupIds: List<Int>): Completable =
        Completable.fromAction { userDao.insertWithGroups(userEntity, groupIds) }

    override fun insertWithGroups(userEntitiesWithGroupIds: Map<UserEntity, List<Int>>): Completable =
    // Completable.create { emitter ->
    //     Timber.d("Completable.create start")
    //     userDao.insertWithGroups(userEntitiesWithGroupIds)
    //     Timber.d("Completable.create before onComplete")
    //     emitter.onComplete()
    //     emitter.
    //     Timber.d("Completable.create after onComplete")
        // }
        Completable.fromAction {
            Timber.d("start action")
            userDao.insertWithGroups(userEntitiesWithGroupIds)
            Timber.d("end action")
        }
}