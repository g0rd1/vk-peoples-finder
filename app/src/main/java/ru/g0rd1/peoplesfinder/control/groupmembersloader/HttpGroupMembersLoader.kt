package ru.g0rd1.peoplesfinder.control.groupmembersloader

import com.google.gson.JsonSyntaxException
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import ru.g0rd1.peoplesfinder.apiservice.response.VkError
import ru.g0rd1.peoplesfinder.base.scheduler.Schedulers
import ru.g0rd1.peoplesfinder.control.groupmembersloader.GroupMembersLoader.Status
import ru.g0rd1.peoplesfinder.db.entity.UserEntity
import ru.g0rd1.peoplesfinder.repo.group.local.LocalGroupsRepo
import ru.g0rd1.peoplesfinder.repo.group.vk.VkGroupsMembersRepo
import ru.g0rd1.peoplesfinder.repo.user.local.LocalUsersRepo
import timber.log.Timber
import java.util.*

class HttpGroupMembersLoader(
    private val groupId: Int,
    private val schedulers: Schedulers,
    private val vkGroupsMembersRepo: VkGroupsMembersRepo,
    private val localUsersRepo: LocalUsersRepo,
    private val localGroupsRepo: LocalGroupsRepo,
    // private val regulator: GroupMembersLoader.Regulator,
    groupMembersCount: Int,
    status: Status,
    loadedCount: Int = 0,
    allMembersLoadedDate: Date?
) : GroupMembersLoader {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val lock = Object()

    @Volatile
    override var allMembersLoadedDate: Date? = allMembersLoadedDate
        @Synchronized set(value) {
            field = value
            onAllMembersLoadedDateListeners.forEach { it(value) }
        }
        @Synchronized get

    @Volatile
    override var status: Status = status
        @Synchronized set(value) {
            field = value
            onStatusChangeListeners.forEach { it(value) }
            if (lockWait) {
                lock.notifyAll()
            }
        }
        @Synchronized get

    @Volatile
    override var loadedMembersCount: Int = loadedCount
        @Synchronized set(value) {
            field = value
            onCountChangeListeners.forEach { it(value) }
        }
        @Synchronized get

    @Volatile
    private var lockWait = false

    private val onCountChangeListeners: MutableList<(count: Int) -> Unit> = mutableListOf()

    private val onStatusChangeListeners: MutableList<(status: Status) -> Unit> = mutableListOf()

    private val onAllMembersLoadedDateListeners: MutableList<(date: Date?) -> Unit> =
        mutableListOf()

    private val queue: MutableMap<Int, Boolean> =
        (0 until groupMembersCount step DOWNLOAD_MEMBERS_STEP)
            .associateWith { it + DOWNLOAD_MEMBERS_STEP <= loadedCount }.toMutableMap()
        @Synchronized get

    override fun pause() {
        if (status != Status.LOAD) return
        synchronized(lock) {
            compositeDisposable.dispose()
            status = Status.PAUSED
        }
    }

    override fun start() {
        if (status == Status.FINISH || status == Status.LOAD) return
        synchronized(lock) block@{
            while (!(status == Status.PAUSED || status == Status.STOPPED)) {
                lock.wait()
                lockWait = true
            }
            lockWait = false
            val nextOffset = getNextOffsetOrNull()
            if (nextOffset == null) {
                finish()
                return@block
            }
            status = Status.LOAD
            load(nextOffset)
        }
    }

    override fun stop() {
        clearOrStop()
    }

    override fun clear() {
        clearOrStop()
        onStatusChangeListeners.clear()
        onCountChangeListeners.clear()
        onAllMembersLoadedDateListeners.clear()
    }

    override fun addOnStatusChangeListener(listener: (status: Status) -> Unit) {
        onStatusChangeListeners.add(listener)
    }

    override fun removeOnStatusChangeListener(listener: (status: Status) -> Unit) {
        onStatusChangeListeners.remove(listener)
    }

    override fun addOnCountChangeListener(listener: (count: Int) -> Unit) {
        onCountChangeListeners.add(listener)
    }

    override fun removeOnCountChangeListener(listener: (count: Int) -> Unit) {
        onCountChangeListeners.remove(listener)
    }

    override fun addOnAllMembersLoadedDateListener(listener: (date: Date?) -> Unit) {
        onAllMembersLoadedDateListeners.add(listener)
    }

    override fun removeOnAllMembersLoadedDateListener(listener: (date: Date?) -> Unit) {
        onAllMembersLoadedDateListeners.remove(listener)
    }

    private fun clearOrStop() {
        if (status == Status.STOPPED || status == Status.STOPPING) return
        synchronized(lock) {
            compositeDisposable.dispose().also { Timber.d("disposed in clearOrStop") }
            status = Status.STOPPING
            Timber.d("clearOrStop()")
            localGroupsRepo.update(groupId, 0, null).retry().blockingAwait()
            if (status != Status.STOPPING) throw IllegalStateException()
            loadedMembersCount = 0
            queue.forEach { (offset, _) ->
                queue[offset] = false
            }
            status = Status.STOPPED
        }
    }

    override fun getGroupId(): Int = groupId

    private fun finish() {
        synchronized(lock) {
            compositeDisposable.dispose().also { Timber.d("disposed in finish") }
            status = Status.FINISH
        }
    }

    @Suppress("RedundantAsSequence")
    private fun getNextOffsetOrNull(): Int? = queue.asSequence().firstOrNull { !it.value }?.key

    private fun load(offset: Int) {
        var loadedMembersCount = 0
        var allMembersLoadedDate: Date? = null
        if (status != Status.LOAD) return
        vkGroupsMembersRepo.getGroupMembers(groupId.toString(), DOWNLOAD_MEMBERS_STEP, offset)
            .flatMapCompletable { users ->
                Timber.d("users downloaded, users size: ${users.size}")
                Timber.d("start insert user with relation")
                localUsersRepo.insertWithGroups(
                    users.map { UserEntity(it) }.associateWith { listOf(groupId) }
                )
                    .andThen(Completable.defer {
                        Timber.d("in defer")
                        Completable.fromAction {
                            Timber.d("finish insert user with relation")
                            Timber.d("start update group info")
                            loadedMembersCount = offset + users.size
                            allMembersLoadedDate = if (isOffsetLast()) {
                                Date()
                            } else {
                                null
                            }
                            Timber.d("debug: loadedMembersCount: $loadedMembersCount, allMembersLoadedDate: $allMembersLoadedDate, groupId: $groupId")
                        }
                    })
            }
            .andThen(Completable.defer {
                localGroupsRepo.update(groupId, loadedMembersCount, allMembersLoadedDate)
            })
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.main())
            .subscribe(
                {
                    Timber.d("finish update group info")
                    this.loadedMembersCount = loadedMembersCount
                    this.allMembersLoadedDate = allMembersLoadedDate
                    queue[offset] = true
                    val nextOffset = getNextOffsetOrNull()
                    if (nextOffset == null) {
                        finish()
                    } else {
                        load(nextOffset)
                    }
                },
                {
                    Timber.e(it)
                    if ((it is VkError && (it.code == VkError.Code.TOO_MANY_REQUESTS_PER_SECOND || it.code == VkError.Code.RATE_LIMIT_REACHED)) || it is JsonSyntaxException) {
                        Timber.d("Retry!")
                        load(offset)
                        return@subscribe
                    }
                    Timber.d("error in loader: $it")
                    Timber.e(it)
                    status = Status.ERROR
                }
            )
            .addTo(compositeDisposable)
    }

    private fun isOffsetLast(): Boolean {
        return queue.values.count { !it } == 1
    }

    companion object {
        private const val DOWNLOAD_MEMBERS_STEP = 10000
    }

}