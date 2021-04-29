package ru.g0rd1.peoplesfinder.db.query

import androidx.room.Embedded
import ru.g0rd1.peoplesfinder.db.entity.GroupInfoEntity
import ru.g0rd1.peoplesfinder.db.entity.GroupEntity

data class GroupEntityAndGroupDataEntity(
    @Embedded
    val groupEntity: GroupEntity,
    @Embedded
    val groupInfoEntity: GroupInfoEntity
)