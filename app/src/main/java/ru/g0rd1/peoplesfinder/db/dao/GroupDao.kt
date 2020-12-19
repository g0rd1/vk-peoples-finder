package ru.g0rd1.peoplesfinder.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import ru.g0rd1.peoplesfinder.db.entity.GroupEntity
import ru.g0rd1.peoplesfinder.db.query.GroupEntityAndGroupDataEntity

@Dao
abstract class GroupDao : BaseDao<GroupEntity>() {

    @Query("SELECT * FROM `group` WHERE id = :id")
    abstract fun get(id: Int): Single<List<GroupEntity>>

    @Query("SELECT * FROM `group`")
    abstract fun get(): Single<List<GroupEntity>>

    @Query("DELETE FROM `group` WHERE id NOT IN (:ids)")
    abstract fun deleteNotIn(ids: List<Int>): Completable

    @Query("SELECT * FROM `group` JOIN `group_data` ON `group`.id = `group_data`.groupId")
    abstract fun getGroupAndGroupData(): Flowable<List<GroupEntityAndGroupDataEntity>>

}