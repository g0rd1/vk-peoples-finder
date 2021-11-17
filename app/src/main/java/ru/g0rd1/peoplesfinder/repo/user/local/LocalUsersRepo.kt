package ru.g0rd1.peoplesfinder.repo.user.local

import io.reactivex.Completable
import io.reactivex.Single
import ru.g0rd1.peoplesfinder.model.Optional
import ru.g0rd1.peoplesfinder.model.User
import ru.g0rd1.peoplesfinder.model.UserType
import ru.g0rd1.peoplesfinder.model.result.UserWithSameGroupsAndUserTypesResult

interface LocalUsersRepo {

    fun getUserWithMaxSameGroupsCountWithFilters(notInUserTypes: List<UserType>): Single<UserWithSameGroupsAndUserTypesResult>
    fun getUsersWithSameGroupsCountWithFilters(
        count: Int,
        notInUserTypes: List<UserType>,
    ): Single<List<UserWithSameGroupsAndUserTypesResult.Success>>  // Single<Map<User, Int>>

    fun insertWithGroups(usersWithGroupIds: Map<User, List<Int>>): Completable
    fun getTypes(id: Int): Single<List<UserType>>
    fun getById(id: Int): Single<Optional<User>>

}