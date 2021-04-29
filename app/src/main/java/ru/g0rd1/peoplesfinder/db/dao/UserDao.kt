package ru.g0rd1.peoplesfinder.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import io.reactivex.Maybe
import ru.g0rd1.peoplesfinder.db.entity.GroupEntity
import ru.g0rd1.peoplesfinder.db.entity.UserEntity
import ru.g0rd1.peoplesfinder.db.entity.UserTypeEntity
import ru.g0rd1.peoplesfinder.db.query.UserEntityWithSameGroupsCount

@Dao
abstract class UserDao : BaseDao<UserEntity>() {

    @Query(
        """
            SELECT * FROM (
                SELECT user_id, COUNT(group_id) as ${UserEntityWithSameGroupsCount.SAME_GROUPS_COUNT_COLUMN_NAME} 
                FROM ${GroupEntity.TABLE_NAME} g JOIN `user_group` ug ON g.id = ug.group_id 
                GROUP BY user_id 
                ORDER BY COUNT(group_id) 
                DESC
            )
            as jn JOIN ${UserEntity.TABLE_NAME} ON jn.user_id = ${UserEntity.TABLE_NAME}.id 
            LIMIT :count 
            OFFSET :offset"""
    )
    abstract fun getWithSameGroupsCount(
        offset: Int,
        count: Int,
    ): Maybe<List<UserEntityWithSameGroupsCount>>

    @Query("SELECT * FROM ${UserEntity.TABLE_NAME}")
    abstract fun get(): Maybe<List<UserEntity>>

    @Query("SELECT * FROM ${UserEntity.TABLE_NAME} WHERE id = :id")
    abstract fun getById(id: Int): Maybe<List<UserEntity>>

    @Query(
        """
            SELECT * FROM (
                SELECT user_type_id 
                FROM ${UserEntity.TABLE_NAME} JOIN `user_user_type` ON ${UserEntity.TABLE_NAME}.id = `user_user_type`.user_id
                WHERE ${UserEntity.TABLE_NAME}.id = :id
            ) 
            jn JOIN ${UserTypeEntity.TABLE_NAME} ON jn.user_type_id = ${UserTypeEntity.TABLE_NAME}.id"""
    )
    abstract fun getUserTypes(id: Int): Maybe<List<UserTypeEntity>>

    @RawQuery
    abstract fun getUsersWithSameGroupsCountByQuery(query: SupportSQLiteQuery): Maybe<List<UserEntityWithSameGroupsCount>>

}