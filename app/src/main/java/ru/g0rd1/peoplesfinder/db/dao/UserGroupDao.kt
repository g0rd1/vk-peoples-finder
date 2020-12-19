package ru.g0rd1.peoplesfinder.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Completable
import ru.g0rd1.peoplesfinder.db.entity.UserGroupEntity

@Dao
abstract class UserGroupDao : BaseDao<UserGroupEntity>() {

    @Query("DELETE FROM `user_group` WHERE group_id = :groupId")
    abstract fun delete(groupId: Int): Completable

}