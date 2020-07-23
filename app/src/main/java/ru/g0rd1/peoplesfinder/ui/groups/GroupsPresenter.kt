package ru.g0rd1.peoplesfinder.ui.groups

import android.content.Context
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
import ru.g0rd1.peoplesfinder.model.Group
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
    private val context: Context,
    private val groupMembersLoaderManager: GroupMembersLoader.Manager
) : GroupsContract.Presenter {

    private lateinit var view: GroupsContract.View

    private lateinit var vkGroups: List<Group>

    private lateinit var groupViews: List<GroupView>

    private lateinit var foundGroupViews: List<GroupView>

    private lateinit var userGroup: List<GroupEntity>

    private lateinit var groupMembersLoaders: List<GroupMembersLoader>

    private val searchSubject: Subject<String> = PublishSubject.create()

    private val searchObservable: Observable<String> = searchSubject

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val groupLoadersWithOnCountChangeListeners: MutableMap<GroupMembersLoader, (count: Int) -> Unit> =
        mutableMapOf()

    private val groupLoadersWithOnStatusChangeListeners: MutableMap<GroupMembersLoader, (status: GroupMembersLoader.Status) -> Unit> =
        mutableMapOf()

    private val groupLoadersWithOnAllMembersLoadedDateListeners: MutableMap<GroupMembersLoader, (date: Date?) -> Unit> =
        mutableMapOf()

    override fun setView(view: GroupsContract.View) {
        this.view = view
    }

    override fun onStart() {
        observeQueryTextChange()
        view.hideContent()
        view.showLoader()
        vkGroupsRepo.getGroups(VKAccessRepo.getUserId())
            .flatMapCompletable { groups ->
                localGroupsRepo.insert(groups.map { GroupEntity(it) })
            }
            .andThen(localGroupsRepo.get())
            .flatMap { groups ->
                val single: Single<Pair<List<GroupEntity>, List<GroupMembersLoader>>> = Single.zip(
                    Single.just(groups),
                    groupMembersLoaderManager.getLoaders(groups.map { it.id }),
                    BiFunction { t1, t2 -> Pair(t1, t2) })
                single
            }
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.main())
            .subscribe(
                { (groups, groupMembersLoaders) ->
                    groupViews = groups.map { GroupView(it) }
                    view.setGroups(groupViews)
                    groupMembersLoaders.forEach { groupMembersLoader ->
                        val associatedGroupView = groupViews.first {
                            groupMembersLoader.getGroupId() == it.id
                        }
                        Timber.d("groupView ${associatedGroupView.name}, status: ${associatedGroupView.status}, loadedMembersCount: ${associatedGroupView.loadedMembersCount}, allMembersLoadedDate: ${associatedGroupView.allMembersLoadedDate}")
                        // associatedGroupView.status = groupMembersLoader.status
                        // associatedGroupView.loadedMembersCount = groupMembersLoader.loadedMembersCount
                        // associatedGroupView.allMembersLoadedDate = groupMembersLoader.allMembersLoadedDate
                        val onCountChangeListener: (Int) -> Unit = {
                            Timber.d("Loaded count changed for groupId: ${associatedGroupView.id}: $it")
                            associatedGroupView.loadedMembersCount = it
                        }
                        val onStatusChangeListener: (GroupMembersLoader.Status) -> Unit = {
                            associatedGroupView.status = it
                        }
                        val onAllMembersLoadedDateListener: (Date?) -> Unit = {
                            associatedGroupView.allMembersLoadedDate = it
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
                    this.groupMembersLoaders = groupMembersLoaders
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

    private fun getVKGroups(onSuccess: ((groups: List<Group>) -> Unit)) {
        vkGroupsRepo.getGroups(VKAccessRepo.getUserId())
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.main())
            .subscribe(
                { groups ->
                    onSuccess(groups)
                },
                {
                    view.hideContent()
                    view.hideLoader()
                    errorHandler.handle(it) { getVKGroups(onSuccess) }
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

    override fun onDownloadGroupMembersButtonClick() {
        groupMembersLoaders.forEach { it.start() }
        // groupMembersLoaders.first().start()
    }

    companion object {
        private const val SEARCH_GROUPS_DELAY_MILLISECONDS = 1000L
        private const val MINIMUM_CHARACTERS_COUNT_FOR_SEARCH = 2
    }
}