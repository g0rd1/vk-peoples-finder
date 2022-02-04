package ru.g0rd1.peoplesfinder.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import ru.g0rd1.peoplesfinder.db.entity.GroupEntity
import ru.g0rd1.peoplesfinder.db.entity.UserEntity
import ru.g0rd1.peoplesfinder.db.entity.UserGroupEntity
import ru.g0rd1.peoplesfinder.db.query.GroupAndGroupData

@Dao
abstract class GroupDao : BaseDao<GroupEntity>() {

    @Query("SELECT * FROM ${GroupEntity.TABLE_NAME} WHERE id = :id")
    abstract fun get(id: Int): Maybe<List<GroupEntity>>

    @Query("SELECT * FROM ${GroupEntity.TABLE_NAME}")
    abstract fun get(): Maybe<List<GroupEntity>>

    @Query("DELETE FROM ${GroupEntity.TABLE_NAME} WHERE ${GroupEntity.TABLE_NAME}.user_in_group = 1 AND id NOT IN (:ids)")
    abstract fun deleteUserGroupsNotIn(ids: List<Int>): Completable

    @Query("SELECT * FROM ${GroupEntity.TABLE_NAME}")
    abstract fun observeGroupAndGroupData(): Flowable<List<GroupAndGroupData>>

    @Query("SELECT * FROM ${GroupEntity.TABLE_NAME}")
    abstract fun getGroupAndGroupData(): Maybe<List<GroupAndGroupData>>

    @Query("SELECT * FROM ${GroupEntity.TABLE_NAME} WHERE id = :id")
    abstract fun getGroupAndGroupData(id: Int): Maybe<List<GroupAndGroupData>>

    @Query(
        """
        SELECT * FROM (
                SELECT group_id FROM (
                    SELECT id FROM ${UserEntity.TABLE_NAME} WHERE id = :userId
                ) ${UserEntity.TABLE_NAME} 
                JOIN 
                ${UserGroupEntity.TABLE_NAME}
                ON
                ${UserEntity.TABLE_NAME}.id = ${UserGroupEntity.TABLE_NAME}.user_id
            ) ${UserGroupEntity.TABLE_NAME}
            JOIN
            ${GroupEntity.TABLE_NAME}
            ON
            ${UserGroupEntity.TABLE_NAME}.group_id = ${GroupEntity.TABLE_NAME}.id
    """
    )
    abstract fun getSameGroupAndGroupDataWithUser(userId: Int): Maybe<List<GroupAndGroupData>>
}