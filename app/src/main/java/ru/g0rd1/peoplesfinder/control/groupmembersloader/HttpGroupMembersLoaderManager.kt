package ru.g0rd1.peoplesfinder.control.groupmembersloader

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject
import ru.g0rd1.peoplesfinder.control.groupmembersloader.GroupMembersLoaderManager.Status
import ru.g0rd1.peoplesfinder.model.Group
import ru.g0rd1.peoplesfinder.repo.group.local.LocalGroupsRepo
import ru.g0rd1.peoplesfinder.ui.synchronization.SynchronizationObserver
import ru.g0rd1.peoplesfinder.util.observeOnUI
import ru.g0rd1.peoplesfinder.util.subscribeOnIo
import timber.log.Timber
import javax.inject.Inject

class HttpGroupMembersLoaderManager @Inject constructor(
    private val groupMembersLoaderFactory: GroupMembersLoader.Factory,
    private val localGroupsRepo: LocalGroupsRepo,
    private val synchronizationObserver: SynchronizationObserver,
) : GroupMembersLoaderManager {

    private val loadStatusSubject: BehaviorSubject<Status> = BehaviorSubject.create()

    private val disposables = CompositeDisposable()

    init {
        observeSynchronization()
    }

    override fun reload() {
        loadStatusSubject.onNext(Status.CommandProcessing)
        disposables.clear()
        clearLoadData().andThen(load())
            .observeOnUI()
            .subscribe(
                {
                    handleLoadResult(it)
                },
                {
                    Timber.e(it)
                    loadStatusSubject.onNext(Status.Error.Generic(it))
                }
            )
            .addTo(disposables)
    }

    override fun startOrContinue() {
        loadStatusSubject.onNext(Status.CommandProcessing)
        disposables.clear()
        load()
            .observeOnUI()
            .subscribe(
                {
                    handleLoadResult(it)
                },
                {
                    Timber.e(it)
                    loadStatusSubject.onNext(Status.Error.Generic(it))
                }
            )
            .addTo(disposables)
    }

    override fun pause() {
        loadStatusSubject.onNext(Status.CommandProcessing)
        disposables.clear()
        loadStatusSubject.onNext(Status.Pause)
    }

    override fun cancel() {
        loadStatusSubject.onNext(Status.CommandProcessing)
        disposables.clear()
        clearLoadData()
            .observeOnUI()
            .subscribe(
                { loadStatusSubject.onNext(Status.Initial) },
                {
                    Timber.e(it)
                    loadStatusSubject.onNext(Status.Error.Generic(it))
                }
            )
            .addTo(disposables)
    }

    override fun observeLoadStatus(): Observable<Status> {
        return loadStatusSubject.subscribeOnIo()
    }

    private fun observeSynchronization() {
        synchronizationObserver.observe()
            .take(1)
            .singleOrError()
            .flatMap {
                localGroupsRepo.get()
            }
            .observeOnUI()
            .subscribe(
                { onGroupsSynchronized(it) },
                {
                    Timber.e(it)
                    loadStatusSubject.onNext(Status.Error.Generic(it))
                }
            )
            .addTo(disposables)
    }

    private fun onGroupsSynchronized(groups: List<Group>) {
        val status = when {
            groups.none { it.allMembersLoadedDate == null } -> Status.Finish
            groups.none { it.allMembersLoadedDate != null } -> Status.Initial
            else -> Status.Pause
        }
        loadStatusSubject.onNext(status)
    }

    private fun load(): Single<GroupMembersLoader.LoadResult> {
        return localGroupsRepo.get().flatMap { groups ->
            val loadSingles: List<Single<GroupMembersLoader.LoadResult>> = groups
                .sortedBy { it.sequentialNumber }
                .map { groupMembersLoaderFactory.create(it.id).load() }
            Completable.fromAction { loadStatusSubject.onNext(Status.Load) }.andThen(
                Flowable
                    .fromIterable(loadSingles)
                    .flatMap({ it.toFlowable() }, MAX_GROUPS_LOADED_CONCURRENTLY)
                    .skipWhile { it is GroupMembersLoader.LoadResult.Success }
                    .first(GroupMembersLoader.LoadResult.Success)
            )
        }
    }

    private fun clearLoadData(): Completable {
        return localGroupsRepo.get().flatMapCompletable { groups ->
            Completable.concat(
                groups.map {
                    localGroupsRepo.updateAllMembersLoadedDate(it.id, null)
                        .andThen(localGroupsRepo.updateLoadedMembersCount(it.id, 0))
                        .andThen(localGroupsRepo.deleteRelation(it.id))
                }
            )
        }
    }

    private fun handleLoadResult(it: GroupMembersLoader.LoadResult?) {
        when (it) {
            GroupMembersLoader.LoadResult.Success -> {
                loadStatusSubject.onNext(Status.Finish)
            }
            is GroupMembersLoader.LoadResult.Error.Vk -> {
                loadStatusSubject.onNext(Status.Error.RateLimitReached)
            }
            is GroupMembersLoader.LoadResult.Error.Generic -> {
                Timber.e(it.throwable)
                loadStatusSubject.onNext(Status.Error.Generic(it.throwable))
            }
        }
    }

    companion object {
        const val MAX_GROUPS_LOADED_CONCURRENTLY = 10
    }

}