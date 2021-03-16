package ru.g0rd1.peoplesfinder.repo.user.local

import io.reactivex.Completable
import io.reactivex.Single
import ru.g0rd1.peoplesfinder.model.Optional
import ru.g0rd1.peoplesfinder.model.User
import ru.g0rd1.peoplesfinder.model.UserType
import ru.g0rd1.peoplesfinder.model.result.UserWithMaxGroupsCountResult

interface LocalUsersRepo {

    fun getWithSameGroupsCount(): Single<Map<User, Int>>
    fun getUserWithMaxGroupsCountWithFilters(): Single<UserWithMaxGroupsCountResult>
    fun insertWithGroups(usersWithGroupIds: Map<User, List<Int>>): Completable
    fun getTypes(id: Int): Single<List<UserType>>
    fun getById(id: Int): Single<Optional<User>>

}