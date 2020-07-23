package ru.g0rd1.peoplesfinder.repo.group.local

import androidx.room.EmptyResultSetException
import io.reactivex.Completable
import io.reactivex.Single
import ru.g0rd1.peoplesfinder.db.dao.GroupDao
import ru.g0rd1.peoplesfinder.db.entity.GroupEntity
import ru.g0rd1.peoplesfinder.model.Optional
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class DBLocalGroupsRepo @Inject constructor(
    private val groupDao: GroupDao
) : LocalGroupsRepo {

    override fun insert(groupEntity: GroupEntity): Completable =
        Completable.fromAction { groupDao.insertOrUpdate(groupEntity) }

    override fun insert(groupEntities: List<GroupEntity>): Completable =
        Completable.fromAction { groupDao.insertOrUpdate(groupEntities) }

    override fun update(groupEntity: GroupEntity): Completable =
        groupDao.update(groupEntity)

    override fun update(
        groupId: Int,
        loadedMembersCount: Int,
        allMembersLoadedDate: Date?
    ): Completable {
        Timber.d("update(groupId: $groupId, loadedMembersCount: $loadedMembersCount, allMembersLoadedDate: $allMembersLoadedDate)")
        Timber.d(Arrays.toString(Thread.currentThread().stackTrace))
        return groupDao.update(groupId, loadedMembersCount, allMembersLoadedDate)
    }

    override fun delete(groupEntity: GroupEntity): Completable = groupDao.delete(groupEntity)

    override fun get(): Single<List<GroupEntity>> = groupDao.get()

    override fun getWithUsers(): Single<List<GroupEntity>> = Single.just(groupDao.getWithUsers())

    override fun insertWithUsers(groupEntity: GroupEntity, userIds: List<Int>): Completable =
        Completable.fromAction { groupDao.insertOrUpdateWithUsers(groupEntity, userIds) }

    override fun insertWithUsers(groupEntitiesWithUserIds: Map<GroupEntity, List<Int>>): Completable =
        Completable.fromAction { groupDao.insertOrUpdateWithUsers(groupEntitiesWithUserIds) }

    override fun get(groupId: Int): Single<Optional<GroupEntity?>> {
        return groupDao.get(groupId)
            .map { Optional(it.firstOrNull()) }
            .onErrorResumeNext { error ->
                if (error is EmptyResultSetException) {
                    val noGroup: Optional<GroupEntity?> = Optional(null)
                    Single.just(noGroup)
                } else {
                    Single.error(error)
                }
            }
    }

    // override fun getWithUsers(groupId: Int): Single<Optional<GroupEntity?>> {
    //     return Single.just(groupDao.getWithUsers(groupId))
    //         .map { Optional(it.firstOrNull()) }
    //         .onErrorResumeNext { error ->
    //             if (error is EmptyResultSetException) {
    //                 val noGroup: Optional<GroupEntity?> = Optional(null)
    //                 Single.just(noGroup)
    //             } else {
    //                 Single.error(error);
    //             }
    //         }
    // }
}