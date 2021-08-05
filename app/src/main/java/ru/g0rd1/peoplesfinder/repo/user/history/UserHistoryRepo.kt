package ru.g0rd1.peoplesfinder.repo.user.history

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import ru.g0rd1.peoplesfinder.model.User

interface UserHistoryRepo {

    fun getLastId(): Maybe<Int>
    fun insert(userId: Int): Completable
    fun deleteAll(): Completable
    fun getUserAndPreviousUsers(historyId: Int, count: Int): Single<Map<Int, User>>
    fun getNextUsers(historyId: Int, count: Int): Single<Map<Int, User>>

}