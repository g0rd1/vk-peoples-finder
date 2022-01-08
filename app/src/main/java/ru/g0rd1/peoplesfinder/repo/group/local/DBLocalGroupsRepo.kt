package ru.g0rd1.peoplesfinder.repo.group.local

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import ru.g0rd1.peoplesfinder.db.dao.GroupDao
import ru.g0rd1.peoplesfinder.db.dao.GroupDataDao
import ru.g0rd1.peoplesfinder.db.dao.UserGroupDao
import ru.g0rd1.peoplesfinder.db.entity.GroupEntity
import ru.g0rd1.peoplesfinder.db.entity.GroupInfoEntity
import ru.g0rd1.peoplesfinder.mapper.GroupMapper
import ru.g0rd1.peoplesfinder.model.Group
import ru.g0rd1.peoplesfinder.model.Optional
import ru.g0rd1.peoplesfinder.util.subscribeOnIo
import java.time.LocalDate
import javax.inject.Inject

class DBLocalGroupsRepo @Inject constructor(
    private val groupDao: GroupDao,
    private val groupDataDao: GroupDataDao,
    private val userGroupDao: UserGroupDao,
    private val groupMapper: GroupMapper,
) : LocalGroupsRepo {

    private var groupCache: List<Group>? = null

    override fun updateLoadedMembersCount(id: Int, loadedMembersCount: Int): Completable =
        groupDataDao.updateLoadedMembersCount(id, loadedMembersCount).subscribeOnIo()

    override fun updateAllMembersLoadedDate(id: Int, allMembersLoadedDate: LocalDate?): Completable =
        groupDataDao.updateAllMembersLoadedDate(id, allMembersLoadedDate).subscribeOnIo()

    override fun updateSequentialNumber(id: Int, sequentialNumber: Int): Completable =
        groupDataDao.updateSequentialNumber(id, sequentialNumber).subscribeOnIo()

    override fun updateHasAccessToMembers(id: Int, hasAccessToMembers: Boolean): Completable =
        groupDataDao.updateHasAccessToMembers(id, hasAccessToMembers).subscribeOnIo()

    override fun insert(groups: List<Group>): Completable {
        return Completable.fromAction { groupDao.insertOrUpdate(groups.toEntities()) }
            .andThen(groupDataDao.insertIfNotExists(groups.toGroupDataEntities()))
            .subscribeOnIo()
    }

    override fun get(): Single<List<Group>> {
        return groupDao.getGroupAndGroupData()
            .map { groupEntityAndGroupDataEntities ->
                groupEntityAndGroupDataEntities.map { (groupEntity, groupDataEntity) ->
                    groupMapper.transform(groupEntity, groupDataEntity)
                }
            }
            .switchIfEmpty(Single.just(listOf()))
            .subscribeOnIo()
    }

    override fun deleteNotIn(ids: List<Int>): Completable =
        groupDao.deleteNotIn(ids).subscribeOnIo()

    override fun deleteRelation(id: Int): Completable =
        userGroupDao.delete(id).subscribeOnIo()

    override fun observeGroups(): Flowable<List<Group>> {
        val groupsFlowable =
            groupDao.observeGroupAndGroupData().map { GroupEntitiesAndGroupDataEntities ->
                GroupEntitiesAndGroupDataEntities.map {
                    groupMapper.transform(
                        it.groupEntity,
                        it.groupInfoEntity
                    )
                }.also { groupCache = it }
            }
        return if (groupCache != null) {
            Flowable.concat(Flowable.just(groupCache), groupsFlowable)
        } else {
            groupsFlowable
        }
            .subscribeOnIo()
    }

    override fun getSameGroupsWithUser(userId: Int): Single<List<Group>> {
        return groupDao.getSameGroupAndGroupDataWithUser(userId)
            .map { groupEntityAndGroupDataEntities ->
                groupEntityAndGroupDataEntities.map { (groupEntity, groupDataEntity) ->
                    groupMapper.transform(groupEntity, groupDataEntity)
                }
            }
            .switchIfEmpty(Single.just(listOf()))
            .subscribeOnIo()
    }

    override fun get(groupId: Int): Single<Optional<Group>> {
        return groupDao.getGroupAndGroupData(groupId)
            .map { GroupEntitiesAndGroupDataEntities ->
                Optional.create(
                    GroupEntitiesAndGroupDataEntities.firstOrNull()
                        ?.let { (groupEntity, groupDataEntity) ->
                            groupMapper.transform(groupEntity, groupDataEntity)
                        }
                )
            }
            .switchIfEmpty(Single.just(Optional.empty()))
            .subscribeOnIo()
    }

    private fun List<Group>.toEntities(): List<GroupEntity> {
        return this.map { it.toEntity() }
    }

    private fun Group.toEntity(): GroupEntity {
        return groupMapper.transformToEntityAndGroupDataEntity(this).first
    }

    private fun List<Group>.toGroupDataEntities(): List<GroupInfoEntity> {
        return this.map { it.toGroupDataEntity() }
    }

    private fun Group.toGroupDataEntity(): GroupInfoEntity {
        return groupMapper.transformToEntityAndGroupDataEntity(this).second
    }

}