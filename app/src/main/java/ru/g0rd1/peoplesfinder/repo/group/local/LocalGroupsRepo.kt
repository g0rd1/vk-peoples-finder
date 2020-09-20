package ru.g0rd1.peoplesfinder.repo.group.local

import io.reactivex.Completable
import io.reactivex.Single
import ru.g0rd1.peoplesfinder.db.entity.GroupEntity
import ru.g0rd1.peoplesfinder.model.Optional
import java.util.*

interface LocalGroupsRepo {

    fun insert(groupEntity: GroupEntity): Completable

    fun insert(groupEntities: List<GroupEntity>): Completable

    fun insertIfNotExists(groupEntities: List<GroupEntity>): Completable

    fun update(
        groupId: Int,
        loadedMembersCount: Int,
        allMembersLoadedDate: Date? = null
    ): Completable

    fun delete(groupEntity: GroupEntity): Completable

    fun get(): Single<List<GroupEntity>>

    fun getWithUsers(): Single<List<GroupEntity>>

    fun insertWithUsers(groupEntity: GroupEntity, userIds: List<Int>): Completable

    fun insertWithUsers(groupEntitiesWithUserIds: Map<GroupEntity, List<Int>>): Completable

    fun get(groupId: Int): Single<Optional<GroupEntity?>>

    companion object {
        const val QUEUE = "QueueLocalGroupsRepo"
    }

}