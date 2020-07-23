package ru.g0rd1.peoplesfinder.db.dao

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Single
import ru.g0rd1.peoplesfinder.db.entity.GroupEntity
import ru.g0rd1.peoplesfinder.db.entity.UserEntity
import ru.g0rd1.peoplesfinder.db.entity.UserGroupCrossRefEntity

@Dao
abstract class UserDao {

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
    abstract fun _insert(userEntity: UserEntity)

    @Suppress("FunctionName")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun _insert(userEntities: List<UserEntity>)

    @Suppress("FunctionName")
    @Query("SELECT * FROM user")
    abstract fun _get(): List<UserEntity>

    @Delete
    abstract fun delete(userEntity: UserEntity): Completable

    @Query("DELETE FROM user WHERE id = :userId")
    abstract fun delete(userId: Int): Completable

    @Suppress("FunctionName")
    @Query("SELECT * FROM user WHERE id = :userId")
    abstract fun _get(userId: Int): List<UserEntity>

    @Suppress("FunctionName")
    @Update
    abstract fun _update(userEntity: UserEntity)

    @Transaction
    open fun insertOrUpdate(userEntity: UserEntity) {
        val currentUserEntity = _get(userEntity.id).firstOrNull()
        if (currentUserEntity == null) {
            _insert(userEntity)
        } else {
            _update(userEntity)
        }
    }

    @Transaction
    open fun insertOrUpdate(userEntities: List<UserEntity>) {
        userEntities.forEach { insertOrUpdate(it) }
    }

    @Transaction
    open fun insertOrUpdateWithGroups(userEntity: UserEntity, groupIds: List<Int>) {
        insertOrUpdate(userEntity)
        groupIds.forEach {
            _insertRelation(UserGroupCrossRefEntity(userEntity.id, it))
        }
    }

    @Transaction
    open fun insertOrUpdateWithGroups(userEntitiesWithGroupIds: Map<UserEntity, List<Int>>) {
        userEntitiesWithGroupIds.forEach { (userEntity, groupIds) ->
            insertOrUpdateWithGroups(userEntity, groupIds)
        }
    }
}