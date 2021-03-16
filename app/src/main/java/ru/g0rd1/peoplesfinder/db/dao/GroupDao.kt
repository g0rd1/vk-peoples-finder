package ru.g0rd1.peoplesfinder.db.dao

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import ru.g0rd1.peoplesfinder.db.entity.GroupEntity
import ru.g0rd1.peoplesfinder.db.query.GroupEntityAndGroupDataEntity

@Dao
abstract class GroupDao : BaseDao<GroupEntity>() {

    @Query("SELECT * FROM `group` WHERE id = :id")
    abstract fun get(id: Int): Maybe<List<GroupEntity>>

    @Query("SELECT * FROM `group`")
    abstract fun get(): Maybe<List<GroupEntity>>

    @Query("DELETE FROM `group` WHERE id NOT IN (:ids)")
    abstract fun deleteNotIn(ids: List<Int>): Completable

    @Query("SELECT * FROM `group` JOIN `group_data` ON `group`.id = `group_data`.groupId")
    abstract fun observeGroupAndGroupData(): Flowable<List<GroupEntityAndGroupDataEntity>>

    @Query("SELECT * FROM `group` JOIN `group_data` ON `group`.id = `group_data`.groupId")
    abstract fun getGroupAndGroupData(): Maybe<List<GroupEntityAndGroupDataEntity>>

    @Query("SELECT * FROM (SELECT * FROM `group` WHERE id = :id) `group` JOIN `group_data` ON `group`.id = `group_data`.groupId")
    abstract fun getGroupAndGroupData(id: Int): Maybe<List<GroupEntityAndGroupDataEntity>>

    @Query("SELECT * FROM (SELECT * FROM (SELECT group_id FROM (SELECT id FROM `user` WHERE id = :userId) `user` JOIN `user_group` ON `user`.id = `user_group`.user_id) `user_group` JOIN `group` ON `user_group`.group_id = `group`.id) `group` JOIN `group_data` ON `group`.id = `group_data`.groupId ")
    abstract fun getSameGroupAndGroupDataWithUser(userId: Int): Maybe<List<GroupEntityAndGroupDataEntity>>
}