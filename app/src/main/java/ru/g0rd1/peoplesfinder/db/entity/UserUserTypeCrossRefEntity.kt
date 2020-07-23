package ru.g0rd1.peoplesfinder.db.entity

import androidx.room.Entity

@Entity(
    primaryKeys = ["userId", "userTypeId"]//,
//    foreignKeys = [
//        ForeignKey(
//            entity = User::class,
//            parentColumns = ["id"],
//            childColumns = ["userId"]
//        ),
//        ForeignKey(
//            entity = UserType::class,
//            parentColumns = ["id"],
//            childColumns = ["userTypeId"]
//        )
//    ]
)
data class UserUserTypeCrossRefEntity(
    val userId: Int,
    val userTypeId: Int
)