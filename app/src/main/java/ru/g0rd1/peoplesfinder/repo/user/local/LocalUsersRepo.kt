package ru.g0rd1.peoplesfinder.repo.user.local

import io.reactivex.Completable
import io.reactivex.Single
import ru.g0rd1.peoplesfinder.model.User

interface LocalUsersRepo {

    fun getWithSameGroupsCount(): Single<Map<User, Int>>

    fun insertWithGroups(usersWithGroupIds: Map<User, List<Int>>): Completable

}