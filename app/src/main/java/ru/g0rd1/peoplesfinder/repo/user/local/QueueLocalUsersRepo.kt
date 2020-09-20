package ru.g0rd1.peoplesfinder.repo.user.local

import io.reactivex.Completable
import io.reactivex.Single
import ru.g0rd1.peoplesfinder.db.entity.UserEntity
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue

class QueueLocalUsersRepo(localUsersRepo: LocalUsersRepo) :
    LocalUsersRepoDecorator(localUsersRepo) {

    private val queueMap: Map<OperationType, BlockingQueue<Any>> =
        mapOf<OperationType, BlockingQueue<Any>>().withDefault {
            ArrayBlockingQueue<Any>(QUEUE_CAPACITY)
        }

    override fun insert(userEntity: UserEntity): Completable {
        return getWithOfferToQueue(localUsersRepo.insert(userEntity), OperationType.INSERT_ENTITY)
    }

    override fun insert(userEntities: List<UserEntity>): Completable {
        return getWithOfferToQueue(
            localUsersRepo.insert(userEntities),
            OperationType.INSERT_ENTITIES
        )
    }

    override fun delete(userEntity: UserEntity): Completable {
        return getWithOfferToQueue(localUsersRepo.delete(userEntity), OperationType.DELETE_ENTITY)
    }

    override fun get(): Single<List<UserEntity>> {
        return getWithOfferToQueue(localUsersRepo.get(), OperationType.GET_ENTITIES)
    }

    override fun getWithSameGroupsCount(): Single<List<UserEntity>> {
        return getWithOfferToQueue(
            localUsersRepo.getWithSameGroupsCount(),
            OperationType.GET_ENTITIES_WITH_SAME_GROUPS_COUNT
        )
    }

    override fun getWithGroups(): Single<List<UserEntity>> {
        return getWithOfferToQueue(
            localUsersRepo.getWithGroups(),
            OperationType.GET_ENTITIES_WITH_GROUPS
        )
    }

    override fun insertWithGroups(userEntity: UserEntity, groupIds: List<Int>): Completable {
        return getWithOfferToQueue(
            localUsersRepo.insertWithGroups(userEntity, groupIds),
            OperationType.INSERT_ENTITY_WITH_GROUPS
        )
    }

    override fun insertWithGroups(userEntitiesWithGroupIds: Map<UserEntity, List<Int>>): Completable {
        return getWithOfferToQueue(
            localUsersRepo.insertWithGroups(
                userEntitiesWithGroupIds
            ),
            OperationType.INSERT_ENTITIES_WITH_GROUPS
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> getWithOfferToQueue(source: T, operationType: OperationType): T {
        return when (source) {
            is Completable -> getCompletableWithOfferToQueue(source, operationType) as T
            is Single<*> -> getSingleWithOfferToQueue(source, operationType) as T
            else -> throw IllegalArgumentException("source must be Completable or Single")
        }
    }

    private fun <T> getSingleWithOfferToQueue(
        single: Single<T>,
        operationType: OperationType
    ): Single<T> {
        return Completable.fromAction { queueMap.getValue(operationType).put(single) }
            .andThen(single)
            .doAfterTerminate { queueMap.getValue(operationType).remove(single) }
    }

    private fun getCompletableWithOfferToQueue(
        completable: Completable,
        operationType: OperationType
    ): Completable {
        return Completable.fromAction { queueMap.getValue(operationType).put(completable) }
            .andThen(completable)
            .doAfterTerminate { queueMap.getValue(operationType).remove(completable) }
    }

    companion object {
        private const val QUEUE_CAPACITY = 1
    }

    private enum class OperationType {
        INSERT_ENTITY,
        INSERT_ENTITIES,
        DELETE_ENTITY,
        GET_ENTITIES,
        GET_ENTITIES_WITH_SAME_GROUPS_COUNT,
        GET_ENTITIES_WITH_GROUPS,
        INSERT_ENTITY_WITH_GROUPS,
        INSERT_ENTITIES_WITH_GROUPS
    }
}