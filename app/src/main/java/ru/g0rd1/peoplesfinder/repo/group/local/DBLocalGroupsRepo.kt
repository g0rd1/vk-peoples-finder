package ru.g0rd1.peoplesfinder.repo.group.local

import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import ru.g0rd1.peoplesfinder.db.dao.GroupDao
import ru.g0rd1.peoplesfinder.db.dao.GroupDataDao
import ru.g0rd1.peoplesfinder.db.dao.GroupHistoryDao
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
    private val groupHistoryDao: GroupHistoryDao,
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

    override fun observeGroups(): Flow<List<Group>> {
        val groupsFlowable =
                groupDao.observeGroupAndGroupData().map { GroupEntitiesAndGroupDataEntities ->
                    GroupEntitiesAndGroupDataEntities.map {
                        groupMapper.transform(
                                it.groupEntity,
                                it.groupInfoEntity
                        )
                    }.also { groupCache = it }
                }
        val groupCache = this.groupCache
        return if (groupCache != null) {
            groupsFlowable.onStart { emit(groupCache) }
        } else {
            groupsFlowable
        }
                .flowOn(Dispatchers.IO)
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

    override fun clearGroupHistory(): Completable {
        return groupHistoryDao.deleteAll().subscribeOnIo()
    }

    override fun getPreviousGroupInHistory(groupId: Int): Single<Optional<Group>> {
        return Single.fromCallable {
            Optional.create(groupHistoryDao.getPreviousGroupIdOrNull(groupId))
        }.flatMap { optionalPreviousGroupId ->
            when (optionalPreviousGroupId) {
                is Optional.Empty -> Single.just(Optional.empty())
                is Optional.Value -> get(optionalPreviousGroupId.value)
            }
        }.subscribeOnIo()
    }

    override fun getNextGroupInHistory(groupId: Int): Single<Optional<Group>> {
        return Single.fromCallable {
            Optional.create(groupHistoryDao.getNextGroupIdOrNull(groupId))
        }.flatMap { optionalNextGroupId ->
            when (optionalNextGroupId) {
                is Optional.Empty -> Single.just(Optional.empty())
                is Optional.Value -> get(optionalNextGroupId.value)
            }
        }.subscribeOnIo()
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