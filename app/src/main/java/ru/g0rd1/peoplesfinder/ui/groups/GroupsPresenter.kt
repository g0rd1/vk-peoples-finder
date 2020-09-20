package ru.g0rd1.peoplesfinder.ui.groups

import android.annotation.SuppressLint
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import ru.g0rd1.peoplesfinder.base.error.Error
import ru.g0rd1.peoplesfinder.base.scheduler.Schedulers
import ru.g0rd1.peoplesfinder.control.groupmembersloader.GroupMembersLoader
import ru.g0rd1.peoplesfinder.db.entity.GroupEntity
import ru.g0rd1.peoplesfinder.repo.access.VKAccessRepo
import ru.g0rd1.peoplesfinder.repo.group.local.LocalGroupsRepo
import ru.g0rd1.peoplesfinder.repo.group.vk.VkGroupsRepo
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class GroupsPresenter @Inject constructor(
    private val vkGroupsRepo: VkGroupsRepo,
    private val localGroupsRepo: LocalGroupsRepo,
    private val VKAccessRepo: VKAccessRepo,
    private val schedulers: Schedulers,
    private val errorHandler: Error.Handler,
    private val groupMembersLoaderManager: GroupMembersLoader.Manager
) : GroupsContract.Presenter {

    private lateinit var view: GroupsContract.View

    private lateinit var groupViewsWithLoaders: Map<GroupView, GroupMembersLoader>

    private lateinit var foundGroupViews: List<GroupView>

    private var groupsView: GroupsView = GroupsView()

    private lateinit var groupsMembersLoader: GroupsMembersSequentialLoader

    private val groupViews: List<GroupView>
        get() = groupViewsWithLoaders.keys.toList()

    private val groupMembersLoaders: List<GroupMembersLoader>
        get() = groupViewsWithLoaders.values.toList()

    private val searchSubject: Subject<String> = PublishSubject.create()

    private val searchObservable: Observable<String> = searchSubject

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val groupLoadersWithOnCountChangeListeners: MutableMap<GroupMembersLoader, (count: Int) -> Unit> =
        mutableMapOf()

    private val groupLoadersWithOnStatusChangeListeners: MutableMap<GroupMembersLoader, (status: GroupMembersLoader.Status) -> Unit> =
        mutableMapOf()

    private val groupLoadersWithOnAllMembersLoadedDateListeners: MutableMap<GroupMembersLoader, (date: Date?) -> Unit> =
        mutableMapOf()

    // private val downloadProgressStatusListener: (status: GroupsMembersSequentialLoader.DownloadProgressStatus) -> Unit = { status ->
    //
    // }
    //
    // private val downloadStatusListener: (status: GroupsMembersSequentialLoader.DownloadStatus) -> Unit = { status ->
    //
    // }

    override fun setView(view: GroupsContract.View) {
        this.view = view
    }

    @SuppressLint("CheckResult")
    override fun onStart() {
        // Observable.interval(1, TimeUnit.SECONDS)
        //     .subscribeOn(schedulers.io())
        //     .observeOn(schedulers.main())
        //     .subscribe{
        //         Timber.d("groupsView: $groupsView")
        //     }
        view.setGroupsView(groupsView)
        observeQueryTextChange()
        view.hideContent()
        view.showLoader()
        var vkGroupsIds: List<Int> = listOf()
        vkGroupsRepo.getGroups(VKAccessRepo.getUserId())
            .flatMapCompletable { groups ->
                vkGroupsIds = groups.map { it.id }
                localGroupsRepo.insertIfNotExists(groups.map { GroupEntity(it) })
            }
            .andThen(localGroupsRepo.get())
            .flatMap { groups ->
                Timber.d("groups: $groups")
                getGroupEntitiesWithLoadersSingle(groups)
            }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.main())
            .subscribe(
                { groupEntitiesWithLoaders: Map<GroupEntity, GroupMembersLoader> ->
                    val sortedGroupEntitiesWithLoaders: Map<GroupEntity, GroupMembersLoader> =
                        vkGroupsIds.associate { groupId ->
                            groupEntitiesWithLoaders.toList().first { it.first.id == groupId }
                        }
                    groupViewsWithLoaders =
                        sortedGroupEntitiesWithLoaders.mapKeys { GroupView(it.key) }
                    groupsMembersLoader =
                        GroupsMembersSequentialLoader(groupMembersLoaders, groupsView, schedulers)
                    updateGroupViews()
                    view.setGroups(groupViews)
                    setUpListeners()
                    view.hideLoader()
                    view.showContent()
                },
                {
                    Timber.e(it)
                    view.hideContent()
                    view.hideLoader()
                    errorHandler.handle(it) { onStart() }
                }
            )
            .addTo(compositeDisposable)
    }

    private fun updateGroupViews() {
        groupViewsWithLoaders.forEach { (groupView, groupMembersLoader) ->
            groupView.loadedMembersCount = groupMembersLoader.loadedMembersCount
            groupView.status = groupMembersLoader.status
            groupView.allMembersLoadedDate = groupMembersLoader.allMembersLoadedDate
        }
    }

    private fun setUpListeners() {
        groupViewsWithLoaders.forEach { (groupView, groupMembersLoader) ->
            val onCountChangeListener: (Int) -> Unit = {
                Timber.d("Loaded count changed for groupId: ${groupView.id}: $it")
                groupView.loadedMembersCount = it
            }
            val onStatusChangeListener: (GroupMembersLoader.Status) -> Unit = {
                Timber.d("status changed for groupId: ${groupView.id}: $it")
                groupView.status = it
            }
            val onAllMembersLoadedDateListener: (Date?) -> Unit = {
                Timber.d("date changed for groupId: ${groupView.id}: $it")
                groupView.allMembersLoadedDate = it
            }
            groupMembersLoader.addOnCountChangeListener(onCountChangeListener)
            groupMembersLoader.addOnStatusChangeListener(onStatusChangeListener)
            groupMembersLoader.addOnAllMembersLoadedDateListener(
                onAllMembersLoadedDateListener
            )
            groupLoadersWithOnCountChangeListeners[groupMembersLoader] =
                onCountChangeListener
            groupLoadersWithOnStatusChangeListeners[groupMembersLoader] =
                onStatusChangeListener
            groupLoadersWithOnAllMembersLoadedDateListeners[groupMembersLoader] =
                onAllMembersLoadedDateListener
        }
    }

    private fun getGroupEntitiesWithLoadersSingle(groups: List<GroupEntity>): Single<MutableMap<GroupEntity, GroupMembersLoader>>? {
        val pairs: List<Single<Pair<GroupEntity, GroupMembersLoader>>> =
            groups.map { groupEntity ->
                Single.zip(
                    Single.just(groupEntity),
                    groupMembersLoaderManager.getLoader(groupEntity.id),
                    BiFunction { t1: GroupEntity, t2: GroupMembersLoader -> Pair(t1, t2) }
                )
            }
        return Observable.fromIterable(pairs)
            .flatMap { it.toObservable() }
            .toMap({ it.first }, { it.second })
    }

    private fun observeQueryTextChange() {
        lateinit var queryText: String
        searchObservable
            .distinctUntilChanged()
            .throttleWithTimeout(SEARCH_GROUPS_DELAY_MILLISECONDS, TimeUnit.MILLISECONDS)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.main())
            .subscribe(
                { query ->
                    foundGroupViews = groupViews.filter { it.name.contains(query, false) }
                    view.setGroups(foundGroupViews)
                    view.hideLoader()
                    view.showContent()
                },
                {
                    view.hideLoader()
                    view.hideContent()
                    errorHandler.handle(it) { searchSubject.onNext(queryText) }
                }
            )
            .addTo(compositeDisposable)
    }

    override fun onStop() {
        groupLoadersWithOnCountChangeListeners.forEach { (groupLoader, listener) ->
            groupLoader.removeOnCountChangeListener(listener)
        }
        groupLoadersWithOnStatusChangeListeners.forEach { (groupLoader, listener) ->
            groupLoader.removeOnStatusChangeListener(listener)
        }
        groupLoadersWithOnAllMembersLoadedDateListeners.forEach { (groupLoader, listener) ->
            groupLoader.removeOnAllMembersLoadedDateListener(listener)
        }
        errorHandler.clear()
        compositeDisposable.clear()
    }

    override fun onQueryTextChange(newText: String?) {
        view.hideContent()
        view.showLoader()
        searchSubject.onNext(newText ?: "")
    }

    override fun onGroupClick(groupView: GroupView) {
        // TODO Действия по нажатию на группу. Возможно показывать более подробную информацию о группе.
    }

    override fun onLoadOrPauseButtonClick() {
        groupsMembersLoader.onStartOrPauseClicked()
        // groupMembersLoaders.forEach { it.start() }
        // groupMembersLoaders.first().start()
    }

    override fun stopButtonClick() {
        groupsMembersLoader.onStopButtonClicked()
    }

    companion object {
        private const val SEARCH_GROUPS_DELAY_MILLISECONDS = 1000L
        private const val MINIMUM_CHARACTERS_COUNT_FOR_SEARCH = 2
    }
}