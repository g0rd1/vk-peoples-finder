package ru.g0rd1.peoplesfinder.repo.user.history

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import ru.g0rd1.peoplesfinder.db.dao.UserHistoryDao
import ru.g0rd1.peoplesfinder.mapper.UserMapper
import ru.g0rd1.peoplesfinder.model.User
import ru.g0rd1.peoplesfinder.util.subscribeOnIo
import javax.inject.Inject

class DBUserHistoryRepo @Inject constructor(
    private val userHistoryDao: UserHistoryDao,
    private val userMapper: UserMapper,
) : UserHistoryRepo {

    override fun getHistoryId(userId: Int): Maybe<Int> {
        return userHistoryDao.getHistoryId(userId).subscribeOnIo()
    }

    override fun getLastId(): Maybe<Int> {
        return userHistoryDao.getLastId().subscribeOnIo()
    }

    override fun insert(userId: Int): Completable {
        return userHistoryDao.insert(userId).onErrorComplete().subscribeOnIo()
    }

    override fun deleteAll(): Completable {
        return userHistoryDao.deleteAll().subscribeOnIo()
    }

    override fun getUserAndPreviousUsers(historyId: Int, count: Int): Single<Map<Int, User>> {
        return userHistoryDao.getUserAndPreviousUsers(historyId, count).map { userEntities ->
            userEntities.associate { it.historyId to userMapper.transform(it.userEntity) }
        }.subscribeOnIo()
    }

    override fun getNextUsers(historyId: Int, count: Int): Single<Map<Int, User>> {
        return userHistoryDao.getNextUsers(historyId, count).map { userEntities ->
            userEntities.associate { it.historyId to userMapper.transform(it.userEntity) }
        }.subscribeOnIo()
    }

}