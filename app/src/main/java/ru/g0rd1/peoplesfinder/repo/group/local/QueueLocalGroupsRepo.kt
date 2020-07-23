package ru.g0rd1.peoplesfinder.repo.group.local

import io.reactivex.Completable
import io.reactivex.Single
import ru.g0rd1.peoplesfinder.db.entity.GroupEntity
import ru.g0rd1.peoplesfinder.model.Optional
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue

class QueueLocalGroupsRepo(localGroupsRepo: LocalGroupsRepo) :
    LocalGroupsRepoDecorator(localGroupsRepo) {

    private val queue: BlockingQueue<Any> = ArrayBlockingQueue(QUEUE_CAPACITY)

    override fun insert(groupEntity: GroupEntity): Completable {
        return getCompletableWithOfferToQueue(localGroupsRepo.insert(groupEntity))
    }

    override fun insert(groupEntities: List<GroupEntity>): Completable {
        return getCompletableWithOfferToQueue(localGroupsRepo.insert(groupEntities))
    }

    override fun update(groupEntity: GroupEntity): Completable {
        return getCompletableWithOfferToQueue(localGroupsRepo.update(groupEntity))
    }

    override fun update(
        groupId: Int,
        loadedMembersCount: Int,
        allMembersLoadedDate: Date?
    ): Completable {
        return getCompletableWithOfferToQueue(
            localGroupsRepo.update(
                groupId,
                loadedMembersCount,
                allMembersLoadedDate
            )
        )
    }

    override fun delete(groupEntity: GroupEntity): Completable {
        return getCompletableWithOfferToQueue(localGroupsRepo.delete(groupEntity))
    }

    override fun get(): Single<List<GroupEntity>> {
        return getSingleWithOfferToQueue(localGroupsRepo.get())
    }

    override fun get(groupId: Int): Single<Optional<GroupEntity?>> {
        return getSingleWithOfferToQueue(localGroupsRepo.get(groupId))
    }

    override fun getWithUsers(): Single<List<GroupEntity>> {
        return getSingleWithOfferToQueue(localGroupsRepo.getWithUsers())
    }

    override fun insertWithUsers(groupEntity: GroupEntity, userIds: List<Int>): Completable {
        return getCompletableWithOfferToQueue(localGroupsRepo.insertWithUsers(groupEntity, userIds))
    }

    override fun insertWithUsers(groupEntitiesWithUserIds: Map<GroupEntity, List<Int>>): Completable {
        return getCompletableWithOfferToQueue(
            localGroupsRepo.insertWithUsers(
                groupEntitiesWithUserIds
            )
        )
    }

    private fun <T> getSingleWithOfferToQueue(single: Single<T>): Single<T> {
        val singleWithActionsAfterTerminate = single.map {
            queue.remove(single)
            it
        }
            .doOnError { queue.remove(single) }
        return Completable.fromAction { queue.put(single) }.andThen(singleWithActionsAfterTerminate)
    }

    private fun getCompletableWithOfferToQueue(completable: Completable): Completable {
        val completableWithActionsAfterTerminate = completable.andThen {
            queue.remove(completable)
        }
            .doOnError { queue.remove(completable) }
        return Completable.fromAction { queue.put(completable) }
            .andThen(completableWithActionsAfterTerminate)
    }

    companion object {
        private const val QUEUE_CAPACITY = 5
    }
}