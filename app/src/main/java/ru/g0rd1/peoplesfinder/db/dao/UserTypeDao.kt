package ru.g0rd1.peoplesfinder.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Maybe
import ru.g0rd1.peoplesfinder.db.entity.UserEntity
import ru.g0rd1.peoplesfinder.db.entity.UserTypeEntity

@Dao
abstract class UserTypeDao: BaseDao<UserTypeEntity>() {

    @Query("SELECT * FROM (SELECT user_id FROM `user_type` JOIN `user_user_type` ON `user_type`.id = `user_user_type`.user_type_id WHERE `user_type`.id = :id) jn JOIN user ON jn.user_id = user.id")
    abstract fun getTypeUsers(id: Int): Maybe<List<UserEntity>>

}