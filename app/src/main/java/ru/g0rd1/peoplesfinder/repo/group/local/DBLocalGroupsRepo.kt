package ru.g0rd1.peoplesfinder.repo.group.local

import androidx.room.EmptyResultSetException
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import ru.g0rd1.peoplesfinder.db.dao.GroupDao
import ru.g0rd1.peoplesfinder.db.dao.GroupDataDao
import ru.g0rd1.peoplesfinder.db.dao.UserGroupDao
import ru.g0rd1.peoplesfinder.db.entity.GroupDataEntity
import ru.g0rd1.peoplesfinder.db.entity.GroupEntity
import ru.g0rd1.peoplesfinder.mapper.GroupMapper
import ru.g0rd1.peoplesfinder.model.Group
import ru.g0rd1.peoplesfinder.model.Optional
import ru.g0rd1.peoplesfinder.util.subscribeOnIo
import java.util.*
import javax.inject.Inject

class DBLocalGroupsRepo @Inject constructor(
    private val groupDao: GroupDao,
    private val groupDataDao: GroupDataDao,
    private val userGroupDao: UserGroupDao,
    private val groupMapper: GroupMapper
) : LocalGroupsRepo {

    override fun updateLoadedMembersCount(id: Int, loadedMembersCount: Int): Completable =
        groupDataDao.updateLoadedMembersCount(id, loadedMembersCount).subscribeOnIo()

    override fun updateAllMembersLoadedDate(id: Int, allMembersLoadedDate: Date?): Completable =
        groupDataDao.updateAllMembersLoadedDate(id, allMembersLoadedDate).subscribeOnIo()

    override fun updateSequentialNumber(id: Int, sequentialNumber: Int): Completable =
        groupDataDao.updateSequentialNumber(id, sequentialNumber)

    override fun updateHasAccessToMembers(id: Int, hasAccessToMembers: Boolean): Completable =
        groupDataDao.updateHasAccessToMembers(id, hasAccessToMembers).subscribeOnIo()

    override fun insert(groups: List<Group>): Completable {
        return Completable.fromAction { groupDao.insertOrUpdate(groups.toEntities()) }
            .andThen(groupDataDao.insertIfNotExists(groups.toGroupDataEntities()))
            .subscribeOnIo()
    }

    override fun get(): Single<List<Group>> {
        return Single.zip(
            groupDao.get(),
            groupDataDao.get(),
            { t1: List<GroupEntity>, t2: List<GroupDataEntity> -> Pair(t1, t2) }
        ).map { (groupEntities, groupDataEntities) ->
            groupEntities.map { groupEntity ->
                val groupDataEntity = groupDataEntities.first { it.groupId == groupEntity.id }
                groupMapper.transform(groupEntity, groupDataEntity)
            }
        }.subscribeOnIo()
    }

    override fun deleteNotIn(ids: List<Int>): Completable =
        groupDao.deleteNotIn(ids).subscribeOnIo()

    override fun deleteRelation(id: Int): Completable =
        userGroupDao.delete(id).subscribeOnIo()

    override fun observeGroups(): Flowable<List<Group>> {
        return groupDao.getGroupAndGroupData().map { GroupEntitiesAndGroupDataEntities ->
            GroupEntitiesAndGroupDataEntities.map {
                groupMapper.transform(
                    it.groupEntity,
                    it.groupDataEntity
                )
            }
        }
    }

    override fun get(groupId: Int): Single<Optional<Group>> {
        return Single.zip(
            groupDao.get(groupId),
            groupDataDao.get(groupId),
            { t1: List<GroupEntity>, t2: List<GroupDataEntity> -> Pair(t1, t2) }
        )
            .map { (groupEntities, groupDataEntities) ->
                Optional.create(
                    groupEntities.firstOrNull()?.let { groupEntity ->
                        val groupDataEntity =
                            groupDataEntities.first { it.groupId == groupEntity.id }
                        groupMapper.transform(groupEntity, groupDataEntity)
                    }
                )
            }
            .onErrorResumeNext { error ->
                if (error is EmptyResultSetException) {
                    Single.just(Optional.empty())
                } else {
                    Single.error(error)
                }
            }.subscribeOnIo()
    }

    private fun List<Group>.toEntities(): List<GroupEntity> {
        return this.map { it.toEntity() }
    }

    private fun Group.toEntity(): GroupEntity {
        return groupMapper.transformToEntityAndGroupDataEntity(this).first
    }

    private fun List<Group>.toGroupDataEntities(): List<GroupDataEntity> {
        return this.map { it.toGroupDataEntity() }
    }

    private fun Group.toGroupDataEntity(): GroupDataEntity {
        return groupMapper.transformToEntityAndGroupDataEntity(this).second
    }

}