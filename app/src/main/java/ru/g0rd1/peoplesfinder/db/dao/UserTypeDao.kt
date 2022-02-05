package ru.g0rd1.peoplesfinder.db.dao

import androidx.room.Dao
import ru.g0rd1.peoplesfinder.db.entity.UserTypeEntity

@Dao
abstract class UserTypeDao: BaseDao<UserTypeEntity>() {

    // @Query(
    //     """
    //         SELECT * FROM (
    //             SELECT user_id FROM ${UserTypeEntity.TABLE_NAME}
    //             JOIN
    //             `user_user_type`
    //             ON
    //             ${UserTypeEntity.TABLE_NAME}.id = `user_user_type`.user_type_id
    //             WHERE ${UserTypeEntity.TABLE_NAME}.id = :id
    //         ) jn
    //         JOIN
    //         ${UserEntity.TABLE_NAME}
    //         ON
    //         jn.user_id = ${UserEntity.TABLE_NAME}.id
    //     """
    // )
    // abstract fun getTypeUsers(id: Int): Maybe<List<UserEntity>>

}