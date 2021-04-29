package ru.g0rd1.peoplesfinder.db.dao

import androidx.room.*
import io.reactivex.Completable

@Dao
abstract class BaseDao<E> {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertIfNotExists(entities: List<E>): Completable

    @Transaction
    open fun insertOrUpdate(entities: List<E>) {
        val insertResult = _insert(entities)
        val updateList = entities.filterIndexed { index, _ ->
            insertResult[index] == -1L
        }
        if (updateList.isNotEmpty()) {
            _update(updateList)
        }
    }

    @Suppress("FunctionName")
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun _insert(entities: List<E>): List<Long>

    @Suppress("FunctionName")
    @Update(onConflict = OnConflictStrategy.IGNORE)
    abstract fun _update(entities: List<E>)

}