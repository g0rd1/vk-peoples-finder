package ru.g0rd1.peoplesfinder.repo.user.local

import io.reactivex.Completable
import io.reactivex.Single
import ru.g0rd1.peoplesfinder.common.PriorityQueueManagerFactory
import ru.g0rd1.peoplesfinder.db.dao.GroupDao
import ru.g0rd1.peoplesfinder.db.dao.UserDao
import ru.g0rd1.peoplesfinder.db.dao.UserGroupDao
import ru.g0rd1.peoplesfinder.db.entity.UserEntity
import ru.g0rd1.peoplesfinder.db.entity.UserGroupEntity
import ru.g0rd1.peoplesfinder.db.entity.UserTypeEntity
import ru.g0rd1.peoplesfinder.db.helper.UserQueryBuilder
import ru.g0rd1.peoplesfinder.mapper.GroupMapper
import ru.g0rd1.peoplesfinder.mapper.UserMapper
import ru.g0rd1.peoplesfinder.model.FilterParameters
import ru.g0rd1.peoplesfinder.model.Optional
import ru.g0rd1.peoplesfinder.model.User
import ru.g0rd1.peoplesfinder.model.UserType
import ru.g0rd1.peoplesfinder.model.result.UserWithSameGroupsCountResult
import ru.g0rd1.peoplesfinder.repo.filters.FiltersRepo
import ru.g0rd1.peoplesfinder.util.subscribeOnIo
import java.time.LocalDate
import javax.inject.Inject

class DBLocalUsersRepo @Inject constructor(
    private val userDao: UserDao,
    private val userGroupDao: UserGroupDao,
    private val groupDao: GroupDao,
    private val userMapper: UserMapper,
    private val groupMapper: GroupMapper,
    private val filtersRepo: FiltersRepo,
    priorityQueueManagerFactory: PriorityQueueManagerFactory,
) : LocalUsersRepo {

    private val insertWithGroupsQueueManager = priorityQueueManagerFactory.create(1)

    override fun getWithSameGroupsCount(): Single<Map<User, Int>> {
        return userDao.getWithSameGroupsCount(0, 50)
            .map { userEntitiesWithSameGroupsCounts ->
                userEntitiesWithSameGroupsCounts
                    .associate { it.userEntity.toUser() to it.sameGroupsCount }
            }
            .switchIfEmpty(Single.just(mapOf()))
            .subscribeOnIo()
    }

    override fun getUserWithMaxSameGroupsCountWithFilters(notInUserTypes: List<UserType>): Single<UserWithSameGroupsCountResult> {
        return userDao.getUsersWithSameGroupsCountByQuery(
            UserQueryBuilder.getUsersQuery(
                filterParameters = filtersRepo.getFilterParameters(),
                count = 1,
                notInUserTypes = notInUserTypes
            )
        ).map { userEntitiesWithSameGroupsCounts ->
            val userEntityWithSameGroupsCounts = userEntitiesWithSameGroupsCounts.first()
            UserWithSameGroupsCountResult.Result(
                userEntityWithSameGroupsCounts.userEntity.toUser(),
                userEntityWithSameGroupsCounts.sameGroupsCount
            )
        }
            .cast(UserWithSameGroupsCountResult::class.java)
            .switchIfEmpty(Single.just(UserWithSameGroupsCountResult.Empty))
            .subscribeOnIo()
    }

    override fun getUsersWithSameGroupsCountWithFilters(
        count: Int,
        notInUserTypes: List<UserType>,
    ): Single<Map<User, Int>> {
        return userDao.getUsersWithSameGroupsCountByQuery(
            UserQueryBuilder.getUsersQuery(
                filterParameters = filtersRepo.getFilterParameters(),
                count = count,
                notInUserTypes = notInUserTypes
            )
        ).map { userEntitiesWithSameGroupsCounts ->
            userEntitiesWithSameGroupsCounts.associate { it.userEntity.toUser() to it.sameGroupsCount }
        }
            .switchIfEmpty(Single.just(mapOf()))
            .subscribeOnIo()
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
    }

    override fun getById(id: Int): Single<Optional<User>> {
        return userDao.getById(id).map { Optional.create(it.firstOrNull()?.toUser()) }
            .switchIfEmpty(Single.just(Optional.empty()))
    }

    private fun getUserWhereConditions(): List<String> {
        val filterParameters = filtersRepo.getFilterParameters()
        val whereConditions: MutableList<String> = mutableListOf()
        getAgeFromWhereConditionOrNull(filterParameters.ageFrom)?.let { whereConditions.add(it) }
        getAgeToWhereConditionOrNull(filterParameters.ageTo)?.let { whereConditions.add(it) }
        getSexWhereConditionOrNull(filterParameters.sex)?.let { whereConditions.add(it) }
        getRelationWhereConditionOrNull(filterParameters.relation)?.let { whereConditions.add(it) }
        getCityWhereConditionOrNull(filterParameters.city)?.let { whereConditions.add(it) }
        getCountryWhereConditionOrNull(filterParameters.country)?.let { whereConditions.add(it) }
        getHasPhotoWhereConditionOrNull(filterParameters)?.let { whereConditions.add(it) }
        return whereConditions
    }

    private fun getAgeFromWhereConditionOrNull(
        ageFrom: FilterParameters.Age,
    ) = when (ageFrom) {
        FilterParameters.Age.Any -> null
        is FilterParameters.Age.Specific -> {
            val ageFromDays = LocalDate.now().minusYears(ageFrom.age.toLong() + 1)
            "${UserEntity.Column.BIRTHDAY_EPOCH_DAYS} > $ageFromDays"
        }
    }

    private fun getAgeToWhereConditionOrNull(
        ageTo: FilterParameters.Age,
    ) = when (ageTo) {
        FilterParameters.Age.Any -> null
        is FilterParameters.Age.Specific -> {
            val ageToDays = LocalDate.now().minusYears(ageTo.age.toLong())
            "${UserEntity.Column.BIRTHDAY_EPOCH_DAYS} <= $ageToDays"
        }
    }

    private fun getSexWhereConditionOrNull(
        sex: FilterParameters.Sex,
    ) = when (sex) {
        FilterParameters.Sex.Any -> null
        is FilterParameters.Sex.Specific -> "${UserEntity.Column.SEX} == '${sex.sex.name}'"
    }

    private fun getRelationWhereConditionOrNull(
        relation: FilterParameters.Relation,
    ) = when (relation) {
        FilterParameters.Relation.Any -> null
        is FilterParameters.Relation.Specific -> "${UserEntity.Column.RELATION} == '${relation.relation.name}'"
    }

    private fun getCityWhereConditionOrNull(
        city: FilterParameters.City,
    ) = when (city) {
        FilterParameters.City.Any -> null
        is FilterParameters.City.Specific -> "${UserEntity.Column.CITY_PREFIX}id == ${city.city.id}"
    }

    private fun getCountryWhereConditionOrNull(
        country: FilterParameters.Country,
    ) = when (country) {
        FilterParameters.Country.Any -> null
        is FilterParameters.Country.Specific -> "${UserEntity.Column.COUNTRY_PREFIX}id == ${country.country.id}"
    }

    private fun getHasPhotoWhereConditionOrNull(filterParameters: FilterParameters) =
        if (filterParameters.hasPhoto) "${UserEntity.Column.HAS_PHOTO} == 1" else null

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