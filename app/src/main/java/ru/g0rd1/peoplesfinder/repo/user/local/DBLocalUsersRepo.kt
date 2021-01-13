package ru.g0rd1.peoplesfinder.repo.user.local

import io.reactivex.Completable
import io.reactivex.Single
import ru.g0rd1.peoplesfinder.common.PriorityQueueManagerFactory
import ru.g0rd1.peoplesfinder.db.dao.UserDao
import ru.g0rd1.peoplesfinder.db.dao.UserGroupDao
import ru.g0rd1.peoplesfinder.db.entity.UserEntity
import ru.g0rd1.peoplesfinder.db.entity.UserGroupEntity
import ru.g0rd1.peoplesfinder.mapper.UserMapper
import ru.g0rd1.peoplesfinder.model.User
import ru.g0rd1.peoplesfinder.util.subscribeOnIo
import javax.inject.Inject

class DBLocalUsersRepo @Inject constructor(
    private val userDao: UserDao,
    private val userGroupDao: UserGroupDao,
    private val userMapper: UserMapper,
    priorityQueueManagerFactory: PriorityQueueManagerFactory
) : LocalUsersRepo {

    private val insertWithGroupsQueueManager = priorityQueueManagerFactory.create(1)

    override fun getWithSameGroupsCount(): Single<Map<User, Int>> {
        return userDao.getWithSameGroupsCount(0, 50)
            .map { userEntitiesWithSameGroupsCounts ->
                userEntitiesWithSameGroupsCounts
                    .associate { it.userEntity.toUser() to it.sameGroupsCount }
            }.subscribeOnIo()
    }

    override fun insertWithGroups(usersWithGroupIds: Map<User, List<Int>>): Completable =
        insertWithGroupsQueueManager.getQueuedCompletable(
            Completable
                .fromAction { userDao.insertOrUpdate(usersWithGroupIds.keys.toList().toEntities()) }
                .andThen(
                    Completable.fromAction {
                        userGroupDao.insertOrUpdate(
                            usersWithGroupIds.flatMap { (user, groupIds) ->
                                groupIds.map { UserGroupEntity(user.id, it) }
                            }
                        )
                    }
                )
        ).subscribeOnIo()


    private fun List<User>.toEntities(): List<UserEntity> {
        return this.map { it.toEntity() }
    }

    private fun User.toEntity(): UserEntity {
        return userMapper.transformToEntity(this)
    }

    private fun List<UserEntity>.toUsers(): List<User> {
        return this.map { it.toUser() }
    }

    private fun UserEntity.toUser(): User {
        return userMapper.transform(this)
    }
}