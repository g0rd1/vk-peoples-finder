package ru.g0rd1.peoplesfinder.db.query

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import ru.g0rd1.peoplesfinder.db.entity.*

data class UserWithSameGroupsAndUserTypes(
    @Embedded
    val userEntity: UserEntity,
    @Relation(
        parentColumn = UserEntity.Column.ID,
        entity = GroupEntity::class,
        entityColumn = GroupEntity.Column.ID,
        associateBy = Junction(
            value = UserGroupEntity::class,
            parentColumn = UserGroupEntity.Column.USER_ID,
            entityColumn = UserGroupEntity.Column.GROUP_ID
        )
    )
    val groupAndGroupData: List<GroupAndGroupData>,
    @Relation(
        parentColumn = UserEntity.Column.ID,
        entity = UserTypeEntity::class,
        entityColumn = UserTypeEntity.Column.ID,
        associateBy = Junction(
            value = UserUserTypeEntity::class,
            parentColumn = UserUserTypeEntity.Column.USER_ID,
            entityColumn = UserUserTypeEntity.Column.USER_TYPE_ID
        )
    )
    val userTypes: List<UserTypeEntity>,
)
