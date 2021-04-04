package ru.g0rd1.peoplesfinder.repo.group.local

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import ru.g0rd1.peoplesfinder.model.Group
import ru.g0rd1.peoplesfinder.model.Optional
import java.time.LocalDate

interface LocalGroupsRepo {

    fun updateLoadedMembersCount(id: Int, loadedMembersCount: Int): Completable

    fun updateAllMembersLoadedDate(id: Int, allMembersLoadedDate: LocalDate?): Completable

    fun updateSequentialNumber(id: Int, sequentialNumber: Int): Completable

    fun updateHasAccessToMembers(id: Int, hasAccessToMembers: Boolean): Completable

    fun insert(groups: List<Group>): Completable

    fun get(): Single<List<Group>>

    fun get(groupId: Int): Single<Optional<Group>>

    fun deleteNotIn(ids: List<Int>): Completable

    fun deleteRelation(id: Int): Completable

    fun observeGroups(): Flowable<List<Group>>

    fun getSameGroupsWithUser(userId: Int): Single<List<Group>>

}