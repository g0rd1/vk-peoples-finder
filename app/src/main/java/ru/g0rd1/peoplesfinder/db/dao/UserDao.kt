package ru.g0rd1.peoplesfinder.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.TypeConverters
import io.reactivex.Maybe
import ru.g0rd1.peoplesfinder.db.converter.UserConverter
import ru.g0rd1.peoplesfinder.db.entity.UserEntity
import ru.g0rd1.peoplesfinder.db.entity.UserTypeEntity
import ru.g0rd1.peoplesfinder.db.query.UserEntityWithSameGroupsCount

@Dao
//@TypeConverters(UserConverter::class)
abstract class UserDao : BaseDao<UserEntity>() {

    @Query("SELECT * FROM (SELECT user_id, COUNT(group_id) as ${UserEntityWithSameGroupsCount.SAME_GROUPS_COUNT_COLUMN_NAME} FROM `group` g JOIN `user_group` ug ON g.id = ug.group_id GROUP BY user_id ORDER BY COUNT(group_id) DESC) as jn JOIN user ON jn.user_id = user.id LIMIT :count OFFSET :offset")
    abstract fun getWithSameGroupsCount(
        offset: Int,
        count: Int
    ): Maybe<List<UserEntityWithSameGroupsCount>>

    @Query("SELECT * FROM user")
    abstract fun get(): Maybe<List<UserEntity>>

    @Query("SELECT * FROM user WHERE id = :id")
    abstract fun getById(id: Int): Maybe<List<UserEntity>>

    @Query("SELECT * FROM (SELECT user_type_id FROM `user` JOIN `user_user_type` ON `user`.id = `user_user_type`.user_id WHERE `user`.id = :id) jn JOIN user_type ON jn.user_type_id = user_type.id")
    abstract fun getUserTypes(id: Int): Maybe<List<UserTypeEntity>>

}