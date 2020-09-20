package ru.g0rd1.peoplesfinder.control.groupmembersloader

import io.reactivex.Completable
import timber.log.Timber
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import javax.inject.Inject

class GroupMembersLoaderRegulator @Inject constructor() : GroupMembersLoader.Regulator {

    private val queue: BlockingQueue<GroupMembersLoader> = ArrayBlockingQueue(QUEUE_CAPACITY)
    @Synchronized get

    private val map: MutableMap<GroupMembersLoader, (status: GroupMembersLoader.Status) -> Unit> =
        mutableMapOf()
        @Synchronized get

    private val onStatusChangeListenerWithLoader: (status: GroupMembersLoader.Status, loader: GroupMembersLoader) -> Unit =
        { status, loader: GroupMembersLoader ->
            if (status != GroupMembersLoader.Status.LOAD) {
                val onStatusChangeListener = map[loader]!!
                loader.removeOnStatusChangeListener(onStatusChangeListener)
                map.remove(loader)
                queue.remove(loader)
                Timber.d("removed loader: $loader, size: ${queue.size}")
            }
        }

    override fun obtainPermissionToLoad(loader: GroupMembersLoader): Completable {
        return Completable.fromAction {
//            Timber.d("obtainPermissionToLoad(loader: $loader) starts")
//            if (queue.contains(loader)
//                    .also { Timber.d("queue.contains(loader): $it") }
//            ) return@fromAction
//            Timber.d("put loader: $loader, size: ${queue.size}")
//            queue.put(loader)
//            Timber.d("putted loader: $loader, size: ${queue.size}")
//            val onStatusChangeListener = getOnStatusChangeListener(loader)
//            map[loader] = onStatusChangeListener
//            loader.addOnStatusChangeListener(onStatusChangeListener)
        }
    }

    private fun getOnStatusChangeListener(loader: GroupMembersLoader): (status: GroupMembersLoader.Status) -> Unit =
        { status ->
            onStatusChangeListenerWithLoader(status, loader)
        }

    companion object {
        private const val QUEUE_CAPACITY = 3
    }
}