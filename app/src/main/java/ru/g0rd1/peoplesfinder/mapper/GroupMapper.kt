package ru.g0rd1.peoplesfinder.mapper

import ru.g0rd1.peoplesfinder.apiservice.model.ApiGroup
import ru.g0rd1.peoplesfinder.db.entity.GroupEntity
import ru.g0rd1.peoplesfinder.db.entity.GroupInfoEntity
import ru.g0rd1.peoplesfinder.model.Group
import javax.inject.Inject

class GroupMapper @Inject constructor() {

    fun transformToEntityAndGroupDataEntity(group: Group): Pair<GroupEntity, GroupInfoEntity> {
        return Pair(
            GroupEntity(
                id = group.id,
                name = group.name,
                deactivated = group.deactivated,
                photo = group.photo,
                type = group.type,
                membersCount = group.membersCount,
                userInGroup = group.userInGroup,
            ),
            GroupInfoEntity(
                groupId = group.id,
                loadedMembersCount = group.loadedMembersCount,
                allMembersLoadedDate = group.allMembersLoadedDate,
                sequentialNumber = group.sequentialNumber,
                hasAccessToMembers = group.hasAccessToMembers,
            )
        )
    }

    fun transform(groupEntity: GroupEntity, groupInfoEntity: GroupInfoEntity): Group {
        return Group(
            id = groupEntity.id,
            name = groupEntity.name,
            deactivated = groupEntity.deactivated,
            photo = groupEntity.photo,
            type = groupEntity.type,
            membersCount = groupEntity.membersCount,
            userInGroup = groupEntity.userInGroup,
            loadedMembersCount = groupInfoEntity.loadedMembersCount,
            allMembersLoadedDate = groupInfoEntity.allMembersLoadedDate,
            sequentialNumber = groupInfoEntity.sequentialNumber,
            hasAccessToMembers = groupInfoEntity.hasAccessToMembers,
        )
    }

    fun transform(
        apiGroup: ApiGroup,
        sequentialNumber: Int?,
        userInGroup: Boolean,
    ): Group {
        return Group(
            id = apiGroup.id,
            name = apiGroup.name,
            deactivated = apiGroup.deactivated,
            photo = apiGroup.photo,
            type = apiGroup.type,
            membersCount = apiGroup.membersCount,
            loadedMembersCount = 0,
            allMembersLoadedDate = null,
            sequentialNumber = sequentialNumber,
            hasAccessToMembers = true,
            userInGroup = userInGroup,
        )
    }

}