package ru.g0rd1.peoplesfinder.db.query

import androidx.room.Embedded
import androidx.room.Relation
import ru.g0rd1.peoplesfinder.db.entity.GroupEntity
import ru.g0rd1.peoplesfinder.db.entity.GroupInfoEntity

data class GroupEntityAndGroupDataEntity(
    @Embedded
    val groupEntity: GroupEntity,
    @Relation(entity = GroupInfoEntity::class, parentColumn = GroupEntity.Column.ID, entityColumn = "groupId")
    val groupInfoEntity: GroupInfoEntity,
)