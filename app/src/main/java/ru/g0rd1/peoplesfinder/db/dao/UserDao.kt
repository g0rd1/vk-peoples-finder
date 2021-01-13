package ru.g0rd1.peoplesfinder.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Single
import ru.g0rd1.peoplesfinder.db.entity.UserEntity
import ru.g0rd1.peoplesfinder.db.query.UserEntityWithSameGroupsCountQuery

@Dao
abstract class UserDao : BaseDao<UserEntity>() {

    @Query("SELECT * FROM (SELECT user_id, COUNT(group_id) as ${UserEntityWithSameGroupsCountQuery.SAME_GROUPS_COUNT_COLUMN_NAME} FROM `group` g JOIN `user_group` ug ON g.id = ug.group_id GROUP BY user_id ORDER BY COUNT(group_id) DESC) as jn JOIN user ON jn.user_id = user.id LIMIT :count OFFSET :offset")
    abstract fun getWithSameGroupsCount(
        offset: Int,
        count: Int
    ): Single<List<UserEntityWithSameGroupsCountQuery>>

    @Query("SELECT * FROM user")
    abstract fun get(): Single<List<UserEntity>>

}