package ru.g0rd1.peoplesfinder.repo.user.local

import io.reactivex.Completable
import io.reactivex.Single
import ru.g0rd1.peoplesfinder.db.entity.UserEntity
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue

class QueueLocalUsersRepo(localUsersRepo: LocalUsersRepo) :
    LocalUsersRepoDecorator(localUsersRepo) {

    private val queue: BlockingQueue<Any> = ArrayBlockingQueue(QUEUE_CAPACITY)

    override fun insert(userEntity: UserEntity): Completable {
        return getCompletableWithOfferToQueue(localUsersRepo.insert(userEntity))
    }

    override fun insert(userEntities: List<UserEntity>): Completable {
        return getCompletableWithOfferToQueue(localUsersRepo.insert(userEntities))
    }

    override fun delete(userEntity: UserEntity): Completable {
        return getCompletableWithOfferToQueue(localUsersRepo.delete(userEntity))
    }

    override fun getUsers(): Single<List<UserEntity>> {
        return getSingleWithOfferToQueue(localUsersRepo.getUsers())
    }

    override fun getUsersWithGroups(): Single<List<UserEntity>> {
        return getSingleWithOfferToQueue(localUsersRepo.getUsersWithGroups())
    }

    override fun insertWithGroups(userEntity: UserEntity, groupIds: List<Int>): Completable {
        return getCompletableWithOfferToQueue(localUsersRepo.insertWithGroups(userEntity, groupIds))
    }

    override fun insertWithGroups(userEntitiesWithGroupIds: Map<UserEntity, List<Int>>): Completable {
        return getCompletableWithOfferToQueue(
            localUsersRepo.insertWithGroups(
                userEntitiesWithGroupIds
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