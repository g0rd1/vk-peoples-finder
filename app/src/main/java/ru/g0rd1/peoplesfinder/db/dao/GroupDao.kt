package ru.g0rd1.peoplesfinder.db.dao

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Single
import ru.g0rd1.peoplesfinder.db.entity.GroupEntity
import ru.g0rd1.peoplesfinder.db.entity.UserEntity
import ru.g0rd1.peoplesfinder.db.entity.UserGroupCrossRefEntity
import timber.log.Timber
import java.util.*

@Dao
abstract class GroupDao {

    @Query("SELECT * FROM `group` WHERE id = :groupId")
    abstract fun get(groupId: Int): Single<List<GroupEntity>>

    @Suppress("FunctionName")
    @Query("SELECT * FROM `group` WHERE id = :groupId")
    abstract fun _get(groupId: Int): List<GroupEntity>

    @Suppress("FunctionName")
    @Query("SELECT * FROM user JOIN user_group ON user_group.user_id = user.id WHERE user_group.group_id = :groupId")
    abstract fun _getGroupUsers(groupId: Int): List<UserEntity>

    @Suppress("FunctionName")
    @Query("SELECT * FROM `group`")
    abstract fun _get(): List<GroupEntity>

    @Suppress("FunctionName")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun _insert(groupEntity: GroupEntity)

    @Suppress("FunctionName")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun _insert(groupEntities: List<GroupEntity>)

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

    // @Transaction
    // open fun getWithUsers(groupId: Int): List<GroupEntity> {
    //     val groupEntities = _get(groupId)
    //     groupEntities.forEach {
    //         it.users = _getGroupUsers(it.id)
    //     }
    //     return groupEntities
    // }

    @Update
    abstract fun update(groupEntity: GroupEntity): Completable

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
    open fun insertOrUpdate(groupEntity: GroupEntity) {
        val currentGroupEntity = _get(groupEntity.id).firstOrNull()
        Timber.d("currentGroupEntity: $currentGroupEntity")
        if (currentGroupEntity == null) {
            _insert(groupEntity)
        } else {
            val groupEntityForUpdate = groupEntity.copy(
                membersCount = currentGroupEntity.membersCount,
                loadedMembersCount = currentGroupEntity.loadedMembersCount,
                allMembersLoadedDate = currentGroupEntity.allMembersLoadedDate
            )
            _update(groupEntityForUpdate)
        }
    }

    @Transaction
    open fun insertOrUpdate(groupEntities: List<GroupEntity>) {
        groupEntities.forEach { insertOrUpdate(it) }
    }

    @Transaction
    open fun insertOrUpdateWithUsers(groupEntity: GroupEntity, userIds: List<Int>) {
        insertOrUpdate(groupEntity)
        userIds.forEach {
            _insertRelation(UserGroupCrossRefEntity(it, groupEntity.id))
        }
    }

    @Transaction
    open fun insertOrUpdateWithUsers(groupEntitiesWithUserIds: Map<GroupEntity, List<Int>>) {
        groupEntitiesWithUserIds.forEach { (groupEntity, userIds) ->
            insertOrUpdateWithUsers(groupEntity, userIds)
        }
    }
}