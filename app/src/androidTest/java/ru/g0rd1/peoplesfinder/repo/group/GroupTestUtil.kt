package ru.g0rd1.peoplesfinder.repo.group

import ru.g0rd1.peoplesfinder.db.entity.GroupEntity
import ru.g0rd1.peoplesfinder.model.Group
import java.util.*

object GroupTestUtil {

    fun getTestGroupEntity(
        id: Int = 0,
        name: String = "",
        deactivated: String? = null,
        photo: String? = null,
        type: String = "",
        membersCount: Int = 0,
        loadedMembersCount: Int = 0,
        allMembersLoadedDate: Date? = null
    ): GroupEntity {
        return GroupEntity(
            id,
            name,
            deactivated,
            photo,
            type,
            membersCount,
            loadedMembersCount,
            allMembersLoadedDate
        )
    }

    fun getTestGroup(
        id: Int = 0,
        name: String = "",
        deactivated: String? = null,
        photo: String? = null,
        type: Group.Type = Group.Type.GROUP,
        membersCount: Int = 0
    ): Group {
        return Group(
            id,
            name,
            deactivated,
            photo,
            type,
            membersCount
        )
    }

}