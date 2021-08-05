package ru.g0rd1.peoplesfinder.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import ru.g0rd1.peoplesfinder.db.entity.UserEntity
import ru.g0rd1.peoplesfinder.db.entity.UserHistoryEntity
import ru.g0rd1.peoplesfinder.db.entity.UserUserTypeEntity
import ru.g0rd1.peoplesfinder.db.query.UserEntityWithHistoryId
import ru.g0rd1.peoplesfinder.model.UserType

@Dao
abstract class UserHistoryDao {

    @Query("SELECT * FROM ${UserHistoryEntity.TABLE_NAME} WHERE ${UserHistoryEntity.Column.USER_ID} = :userId")
    abstract fun getUserHistoryEntity(userId: Int): Maybe<UserHistoryEntity>

    @Query("DELETE FROM ${UserHistoryEntity.TABLE_NAME}")
    abstract fun deleteAll(): Completable

    @Query("INSERT INTO ${UserHistoryEntity.TABLE_NAME} (${UserHistoryEntity.Column.USER_ID}) VALUES (:userId)")
    abstract fun insert(userId: Int): Completable

    @Query("SELECT ${UserHistoryEntity.Column.ID} FROM ${UserHistoryEntity.TABLE_NAME} ORDER BY ${UserHistoryEntity.Column.ID} DESC LIMIT 1")
    abstract fun getLastId(): Maybe<Int>

    // @Transaction
    // open fun getPreviousUsers(userId: Int, count: Int): List<UserEntity> {
    //     val userHistoryEntity = _getHistoryIdOrNull(userId) ?: throw Throwable("No user with id $userId")
    //     return _getPreviousUserOrNull(userHistoryEntity.id, count)
    // }
    //
    // @Transaction
    // open fun getNextUsers(userId: Int, count: Int): List<UserEntity> {
    //     val userHistoryEntity = _getHistoryIdOrNull(userId) ?: throw Throwable("No user with id $userId")
    //     return _getNextUserOrNull(userHistoryEntity.id, count)
    // }

    @Query(
        """
            SELECT * FROM
            (SELECT * FROM ${UserEntity.TABLE_NAME} JOIN ${UserUserTypeEntity.TABLE_NAME} ON ${UserEntity.TABLE_NAME}.${UserEntity.Column.ID} = ${UserUserTypeEntity.TABLE_NAME}.${UserUserTypeEntity.Column.USER_ID} WHERE ${UserUserTypeEntity.TABLE_NAME}.${UserUserTypeEntity.Column.USER_TYPE_ID} != ${UserType.BLOCKED_ID}) as t
            JOIN
            (SELECT ${UserHistoryEntity.Column.USER_ID}, ${UserHistoryEntity.Column.ID} as ${UserEntityWithHistoryId.HISTORY_ID_COLUMN_NAME} FROM ${UserHistoryEntity.TABLE_NAME} WHERE ${UserHistoryEntity.Column.ID} <= :historyId ORDER BY ${UserHistoryEntity.Column.ID} DESC LIMIT :count) as k
            ON
            t.${UserEntity.Column.ID} = k.${UserHistoryEntity.Column.USER_ID}
        """
    )
    abstract fun getUserAndPreviousUsers(historyId: Int, count: Int): Single<List<UserEntityWithHistoryId>>

    @Query(
        """
            SELECT * FROM
            (SELECT * FROM ${UserEntity.TABLE_NAME} JOIN ${UserUserTypeEntity.TABLE_NAME} ON ${UserEntity.TABLE_NAME}.${UserEntity.Column.ID} = ${UserUserTypeEntity.TABLE_NAME}.${UserUserTypeEntity.Column.USER_ID} WHERE ${UserUserTypeEntity.TABLE_NAME}.${UserUserTypeEntity.Column.USER_TYPE_ID} != ${UserType.BLOCKED_ID}) as t
            JOIN
            (SELECT ${UserHistoryEntity.Column.USER_ID}, ${UserHistoryEntity.Column.ID} as ${UserEntityWithHistoryId.HISTORY_ID_COLUMN_NAME} FROM ${UserHistoryEntity.TABLE_NAME} WHERE ${UserHistoryEntity.Column.ID} > :historyId ORDER BY ${UserHistoryEntity.Column.ID} ASC LIMIT :count) as k
            ON
            t.${UserEntity.Column.ID} = k.${UserHistoryEntity.Column.USER_ID}
        """
    )
    abstract fun getNextUsers(historyId: Int, count: Int): Single<List<UserEntityWithHistoryId>>
    //
    // @Query("SELECT * FROM ${UserHistoryEntity.TABLE_NAME} WHERE ${UserHistoryEntity.Column.USER_ID} = :userId" )
    // @Suppress("FunctionName")
    // abstract fun _getHistoryIdOrNull(userId: Int): UserHistoryEntity?

}