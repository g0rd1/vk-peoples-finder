package ru.g0rd1.peoplesfinder.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import io.reactivex.Completable
import io.reactivex.Maybe
import ru.g0rd1.peoplesfinder.db.entity.GroupEntity
import ru.g0rd1.peoplesfinder.db.entity.GroupHistoryEntity

@Dao
abstract class GroupHistoryDao: BaseDao<GroupEntity>() {

    @Query("SELECT * FROM ${GroupHistoryEntity.TABLE_NAME} WHERE ${GroupHistoryEntity.Column.GROUP_ID} = :groupId" )
    abstract fun getHistoryId(groupId: Int): Maybe<List<GroupHistoryEntity>>

    @Query("DELETE FROM ${GroupHistoryEntity.TABLE_NAME}")
    abstract fun deleteAll(): Completable

    @Transaction
    open fun getPreviousGroupIdOrNull(groupId: Int): Int? {
        val groupHistoryEntity = _getHistoryIdOrNull(groupId) ?: throw Throwable("No group with id $groupId")
        return _getPreviousGroupOrNull(groupHistoryEntity.id)?.id
    }

    @Transaction
    open fun getNextGroupIdOrNull(groupId: Int): Int? {
        val groupHistoryEntity = _getHistoryIdOrNull(groupId) ?: throw Throwable("No group with id $groupId")
        return _getNextGroupOrNull(groupHistoryEntity.id)?.id
    }

    @Query(
        """
            SELECT * FROM ${GroupEntity.TABLE_NAME}
            JOIN
            (SELECT ${GroupHistoryEntity.Column.GROUP_ID} FROM ${GroupHistoryEntity.TABLE_NAME} WHERE ${GroupHistoryEntity.Column.ID} < :historyId ORDER BY ${GroupHistoryEntity.Column.ID} DESC LIMIT 1) as k
            ON
            ${GroupEntity.TABLE_NAME}.${GroupEntity.Column.ID} = k.${GroupHistoryEntity.Column.GROUP_ID}
        """
    )
    @Suppress("FunctionName")
    abstract fun _getPreviousGroupOrNull(historyId: Int): GroupEntity?

    @Query(
        """
            SELECT * FROM ${GroupEntity.TABLE_NAME}
            JOIN
            (SELECT ${GroupHistoryEntity.Column.GROUP_ID} FROM ${GroupHistoryEntity.TABLE_NAME} WHERE ${GroupHistoryEntity.Column.ID} < :historyId ORDER BY ${GroupHistoryEntity.Column.ID} ASC LIMIT 1) as k
            ON
            ${GroupEntity.TABLE_NAME}.${GroupEntity.Column.ID} = k.${GroupHistoryEntity.Column.GROUP_ID}
        """
    )
    @Suppress("FunctionName")
    abstract fun _getNextGroupOrNull(historyId: Int): GroupEntity?

    @Query("SELECT * FROM ${GroupHistoryEntity.TABLE_NAME} WHERE ${GroupHistoryEntity.Column.GROUP_ID} = :groupId" )
    @Suppress("FunctionName")
    abstract fun _getHistoryIdOrNull(groupId: Int): GroupHistoryEntity?

}