package ru.g0rd1.peoplesfinder.repo.user.local

import io.reactivex.Completable
import io.reactivex.Single
import ru.g0rd1.peoplesfinder.common.PriorityQueueManagerFactory
import ru.g0rd1.peoplesfinder.db.dao.GroupDao
import ru.g0rd1.peoplesfinder.db.dao.UserDao
import ru.g0rd1.peoplesfinder.db.dao.UserGroupDao
import ru.g0rd1.peoplesfinder.db.dao.UserUserTypeDao
import ru.g0rd1.peoplesfinder.db.entity.UserEntity
import ru.g0rd1.peoplesfinder.db.entity.UserGroupEntity
import ru.g0rd1.peoplesfinder.db.entity.UserTypeEntity
import ru.g0rd1.peoplesfinder.db.entity.UserUserTypeEntity
import ru.g0rd1.peoplesfinder.mapper.GroupMapper
import ru.g0rd1.peoplesfinder.mapper.UserMapper
import ru.g0rd1.peoplesfinder.model.Optional
import ru.g0rd1.peoplesfinder.model.User
import ru.g0rd1.peoplesfinder.model.UserType
import ru.g0rd1.peoplesfinder.model.result.UserWithSameGroupsAndUserTypesResult
import ru.g0rd1.peoplesfinder.repo.filters.FiltersRepo
import ru.g0rd1.peoplesfinder.util.subscribeOnIo
import javax.inject.Inject

class DBLocalUsersRepo @Inject constructor(
    private val userDao: UserDao,
    private val userGroupDao: UserGroupDao,
    private val groupDao: GroupDao,
    private val userMapper: UserMapper,
    private val groupMapper: GroupMapper,
    private val filtersRepo: FiltersRepo,
    private val userUserTypeDao: UserUserTypeDao,
    priorityQueueManagerFactory: PriorityQueueManagerFactory,
) : LocalUsersRepo {

    private val insertWithGroupsQueueManager = priorityQueueManagerFactory.create(1)

    override fun getUserWithMaxSameGroupsCountWithFilters(notInUserTypes: List<UserType>): Single<UserWithSameGroupsAndUserTypesResult> {
        return getUsersWithSameGroupsCountWithFilters(count = 1, notInUserTypes = notInUserTypes).map {
            if (it.isEmpty()) {
                UserWithSameGroupsAndUserTypesResult.Empty
            } else {
                it.first()
            }
        }
    }

    override fun getUsersWithSameGroupsCountWithFilters(
        count: Int,
        notInUserTypes: List<UserType>,
    ): Single<List<UserWithSameGroupsAndUserTypesResult.Success>> {
        return Single.fromCallable {
            userDao.getUsersWithSameGroupsAndUserTypes(
                filterParameters = filtersRepo.getFilterParameters(),
                count = count,
                notInUserTypes = notInUserTypes
            ).map { userEntitiesWithSameGroupsCounts ->
                UserWithSameGroupsAndUserTypesResult.Success(
                    user = userEntitiesWithSameGroupsCounts.userEntity.toUser(),
                    sameGroups = userEntitiesWithSameGroupsCounts.groupAndGroupData.map { groupMapper.transform(it.groupEntity, it.groupInfoEntity) },
                    userTypes = userEntitiesWithSameGroupsCounts.userTypes.map { it.toUserTypes() }
                )
            }
        }.subscribeOnIo()
    }

    override fun insertWithGroups(usersWithGroupIds: Map<User, List<Int>>): Completable =
        insertWithGroupsQueueManager.getQueuedCompletable(
            completable = Completable
                .fromAction { userDao.insertOrUpdate(usersWithGroupIds.keys.toList().toEntities()) }
                .andThen(
                    Completable.fromAction {
                        userGroupDao.insertOrUpdate(
                            usersWithGroupIds.flatMap { (user, groupIds) ->
                                groupIds.map { UserGroupEntity(user.id, it) }
                            }
                        )
                    }
                ),
            priority = 0
        ).subscribeOnIo()

    override fun getTypes(id: Int): Single<List<UserType>> {
        return userDao.getUserTypes(id).map { it.toUserTypes() }
            .switchIfEmpty(Single.just(listOf()))
            .subscribeOnIo()
    }

    override fun getById(id: Int): Single<Optional<User>> {
        return userDao.getById(id).map { Optional.create(it.firstOrNull()?.toUser()) }
            .switchIfEmpty(Single.just(Optional.empty()))
            .subscribeOnIo()
    }

    override fun switchTypeStatus(userId: Int, userType: UserType): Completable {
        return Completable.fromAction {
            userUserTypeDao.insertOrDelete(listOf(UserUserTypeEntity(userId, userType.id)))
        }.subscribeOnIo()
    }

    override fun getUserByType(userTypeId: Int): Single<List<User>> {
        return userDao.getByUserType(userTypeId).map { userEntities ->
            userEntities.map { it.toUser() }
        }.subscribeOnIo()
    }

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

    private fun List<UserTypeEntity>.toUserTypes(): List<UserType> {
        return this.map { it.toUserTypes() }
    }

    private fun UserTypeEntity.toUserTypes(): UserType {
        return userMapper.transform(this)
    }

}