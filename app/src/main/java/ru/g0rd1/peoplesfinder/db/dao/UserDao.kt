package ru.g0rd1.peoplesfinder.db.dao

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Single
import ru.g0rd1.peoplesfinder.db.entity.GroupEntity
import ru.g0rd1.peoplesfinder.db.entity.UserEntity
import ru.g0rd1.peoplesfinder.db.entity.UserGroupCrossRefEntity
import ru.g0rd1.peoplesfinder.db.query.UserEntityWithSameGroupsCountQuery

@Dao
abstract class UserDao {

    @Query("SELECT * FROM (SELECT user_id, COUNT(group_id) as ${UserEntityWithSameGroupsCountQuery.SAME_GROUPS_COUNT_COLUMN_NAME} FROM `group` g JOIN `user_group` ug ON g.id = ug.group_id GROUP BY user_id ORDER BY COUNT(group_id) DESC) as jn JOIN user ON jn.user_id = user.id;")
    abstract fun getWithSameGroupsCount(): Single<List<UserEntityWithSameGroupsCountQuery>>

    @Transaction
    open fun getWithGroups(): List<UserEntity> {
        val userEntities = _get()
        userEntities.forEach {
            it.groups = _getUserGroups(it.id)
        }
        return userEntities
    }

    @Query("SELECT * FROM user")
    abstract fun get(): Single<List<UserEntity>>

    @Suppress("FunctionName")
    @Query("SELECT * FROM `group` JOIN user_group ON user_group.group_id = `group`.id WHERE user_group.user_id = :userId")
    abstract fun _getUserGroups(userId: Int): List<GroupEntity>

    @Suppress("FunctionName")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun _insertRelation(userGroupEntity: UserGroupCrossRefEntity)

    @Suppress("FunctionName")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(userEntity: UserEntity): Completable

    @Suppress("FunctionName")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(userEntities: List<UserEntity>): Completable

    @Suppress("FunctionName")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun _insert(userEntity: UserEntity)

    @Suppress("FunctionName")
    @Query("SELECT * FROM user")
    abstract fun _get(): List<UserEntity>

    @Delete
    abstract fun delete(userEntity: UserEntity): Completable

    @Query("DELETE FROM user WHERE id = :userId")
    abstract fun delete(userId: Int): Completable

    @Transaction
    open fun insertWithGroups(userEntity: UserEntity, groupIds: List<Int>) {
        _insert(userEntity)
        groupIds.forEach {
            _insertRelation(UserGroupCrossRefEntity(userEntity.id, it))
        }
    }

    @Transaction
    open fun insertWithGroups(userEntitiesWithGroupIds: Map<UserEntity, List<Int>>) {
        userEntitiesWithGroupIds.forEach { (userEntity, groupIds) ->
            insertWithGroups(userEntity, groupIds)
        }
    }
}