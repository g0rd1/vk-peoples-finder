package ru.g0rd1.peoplesfinder.db.dao

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Single
import ru.g0rd1.peoplesfinder.db.entity.GroupEntity
import ru.g0rd1.peoplesfinder.db.entity.UserEntity
import ru.g0rd1.peoplesfinder.db.entity.UserGroupCrossRefEntity
import java.util.*

@Dao
abstract class GroupDao {

    @Query("SELECT * FROM `group` WHERE id = :groupId")
    abstract fun get(groupId: Int): Single<List<GroupEntity>>

    @Suppress("FunctionName")
    @Query("SELECT * FROM `group`")
    abstract fun _get(): List<GroupEntity>

    @Suppress("FunctionName")
    @Query("SELECT * FROM user JOIN user_group ON user_group.user_id = user.id WHERE user_group.group_id = :groupId")
    abstract fun _getGroupUsers(groupId: Int): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(groupEntity: GroupEntity): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(groupEntities: List<GroupEntity>): Completable

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertIfNotExists(groupEntities: List<GroupEntity>): Completable

    @Suppress("FunctionName")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun _insert(groupEntity: GroupEntity)

    @Suppress("FunctionName")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun _insertRelation(userGroupEntity: UserGroupCrossRefEntity)

    @Delete
    abstract fun delete(groupEntity: GroupEntity): Completable

    @Query("DELETE FROM `group` WHERE id = :groupId")
    abstract fun delete(groupId: Int): Completable

    @Query("SELECT * FROM `group`")
    abstract fun get(): Single<List<GroupEntity>>

    @Transaction
    open fun getWithUsers(): List<GroupEntity> {
        val groupEntities = _get()
        groupEntities.forEach {
            it.users = _getGroupUsers(it.id)
        }
        return groupEntities
    }

    @Update(onConflict = OnConflictStrategy.REPLACE)
    @Suppress("FunctionName")
    abstract fun _update(groupEntity: GroupEntity)

    @Query("UPDATE `group` SET loaded_members_count = :loadedMembersCount, all_members_loaded_date = :allMembersLoadedDate WHERE id = :groupId")
    abstract fun update(
        groupId: Int,
        loadedMembersCount: Int,
        allMembersLoadedDate: Date?
    ): Completable

    @Transaction
    open fun insertWithUsers(groupEntity: GroupEntity, userIds: List<Int>) {
        _insert(groupEntity)
        userIds.forEach {
            _insertRelation(UserGroupCrossRefEntity(it, groupEntity.id))
        }
    }

    @Transaction
    open fun insertWithUsers(groupEntitiesWithUserIds: Map<GroupEntity, List<Int>>) {
        groupEntitiesWithUserIds.forEach { (groupEntity, userIds) ->
            insertWithUsers(groupEntity, userIds)
        }
    }
}