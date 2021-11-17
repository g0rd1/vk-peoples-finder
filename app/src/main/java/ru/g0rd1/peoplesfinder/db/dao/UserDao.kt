package ru.g0rd1.peoplesfinder.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SupportSQLiteQuery
import io.reactivex.Maybe
import ru.g0rd1.peoplesfinder.db.entity.UserEntity
import ru.g0rd1.peoplesfinder.db.entity.UserTypeEntity
import ru.g0rd1.peoplesfinder.db.helper.UserQueryBuilder
import ru.g0rd1.peoplesfinder.db.query.UserIdWithSameGroupsCount
import ru.g0rd1.peoplesfinder.db.query.UserWithSameGroupsAndUserTypes
import ru.g0rd1.peoplesfinder.model.FilterParameters
import ru.g0rd1.peoplesfinder.model.UserType

@Dao
abstract class UserDao : BaseDao<UserEntity>() {

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

    @Transaction
    open fun getUsersWithSameGroupsAndUserTypes(
        filterParameters: FilterParameters,
        count: Int,
        notInUserTypes: List<UserType>,
    ): List<UserWithSameGroupsAndUserTypes> {
        val usersIdsWithSameGroupsCount = _getUsersIdsWithSameGroupsCountByQuery(
            UserQueryBuilder.getUsersQuery(
                filterParameters = filterParameters,
                count = count,
                notInUserTypes = notInUserTypes
            )
        )
        return _getUsers(usersIdsWithSameGroupsCount.map { it.userId })
    }

    @Query("SELECT * FROM ${UserEntity.TABLE_NAME} WHERE ${UserEntity.Column.ID} IN (:userIds)")
    @Suppress("FunctionName")
    abstract fun _getUsers(userIds: List<Int>): List<UserWithSameGroupsAndUserTypes>

    @RawQuery
    @Suppress("FunctionName")
    abstract fun _getUsersIdsWithSameGroupsCountByQuery(query: SupportSQLiteQuery): List<UserIdWithSameGroupsCount>

}