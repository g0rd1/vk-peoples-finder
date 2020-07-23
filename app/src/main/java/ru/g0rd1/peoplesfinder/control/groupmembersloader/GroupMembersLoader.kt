package ru.g0rd1.peoplesfinder.control.groupmembersloader

import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

interface GroupMembersLoader {

    var status: Status
    var loadedMembersCount: Int
    var allMembersLoadedDate: Date?

    fun pause()
    fun start()
    fun stop()
    fun clear()
    fun addOnStatusChangeListener(listener: (status: Status) -> Unit)
    fun removeOnStatusChangeListener(listener: (status: Status) -> Unit)
    fun addOnCountChangeListener(listener: (count: Int) -> Unit)
    fun removeOnCountChangeListener(listener: (count: Int) -> Unit)
    fun addOnAllMembersLoadedDateListener(listener: (date: Date?) -> Unit)
    fun removeOnAllMembersLoadedDateListener(listener: (date: Date?) -> Unit)
    fun getGroupId(): Int

    interface Factory {
        fun create(groupId: Int): Single<GroupMembersLoader>
    }

    interface Manager {
        fun getLoader(groupId: Int): Single<GroupMembersLoader>
        fun getLoaders(groupIds: List<Int>): Single<List<GroupMembersLoader>>
        fun clear()
    }

    interface Regulator {
        fun obtainPermissionToLoad(loader: GroupMembersLoader): Completable
    }

    enum class Status {
        STOPPED, STOPPING, PAUSED, LOAD, FINISH, ERROR
    }

}