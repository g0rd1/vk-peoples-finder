package ru.g0rd1.peoplesfinder.control.groupmembersloader

import io.reactivex.Single
import javax.inject.Inject

class GroupMembersLoaderManager @Inject constructor(
    private val groupMembersLoaderFactory: GroupMembersLoader.Factory
) : GroupMembersLoader.Manager {

    private val loadersMap: MutableMap<Int, Single<GroupMembersLoader>> = mutableMapOf()

    override fun getLoader(groupId: Int): Single<GroupMembersLoader> {
        if (loadersMap.containsKey(groupId)) {
            return loadersMap[groupId]!!
        }
        val loader = groupMembersLoaderFactory.create(groupId)
        loadersMap[groupId] = loader
        return loader
    }

    @Suppress("UNCHECKED_CAST")
    override fun getLoaders(groupIds: List<Int>): Single<List<GroupMembersLoader>> {
        val list: List<Single<GroupMembersLoader>> = groupIds.map { getLoader(it) }
        return Single.zip(list) { t: Array<out Any> -> t.toList() as List<GroupMembersLoader> }
    }

    override fun clear() {
        loadersMap.clear()
    }
}