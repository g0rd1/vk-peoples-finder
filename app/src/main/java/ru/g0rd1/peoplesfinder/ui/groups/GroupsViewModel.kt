package ru.g0rd1.peoplesfinder.ui.groups

import androidx.databinding.*
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import ru.g0rd1.peoplesfinder.R
import ru.g0rd1.peoplesfinder.base.error.Error
import ru.g0rd1.peoplesfinder.common.ResourceManager
import ru.g0rd1.peoplesfinder.control.groupmembersloader.GroupMembersLoaderManager
import ru.g0rd1.peoplesfinder.repo.group.local.LocalGroupsRepo
import ru.g0rd1.peoplesfinder.util.observeOnUI
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class GroupsViewModel @Inject constructor(
    private val groupMembersLoaderManager: GroupMembersLoaderManager,
    private val localGroupsRepo: LocalGroupsRepo,
    private val errorHandler: Error.Handler,
    private val resourceManager: ResourceManager
) : ViewModel() {

    val buttonsVisible: ObservableBoolean = ObservableBoolean(false)
    val cancelButtonVisible: ObservableBoolean = ObservableBoolean(false)
    val startButtonVisible: ObservableBoolean = ObservableBoolean(false)
    val reloadButtonVisible: ObservableBoolean = ObservableBoolean(false)
    val continueButtonVisible: ObservableBoolean = ObservableBoolean(false)
    val pauseButtonVisible: ObservableBoolean = ObservableBoolean(false)

    val errorTextVisible: ObservableBoolean = ObservableBoolean(false)
    val errorText: ObservableField<String> = ObservableField("")

    val membersCount: ObservableInt = ObservableInt(0)
    val loadedMembersCount: ObservableInt = ObservableInt(0)

    val groupViewModels: ObservableList<GroupViewModel> = ObservableArrayList()

    private val disposables = CompositeDisposable()

    init {
        observer()
    }

    fun startLoad() {
        groupMembersLoaderManager.startOrContinue()
    }

    fun continueLoad() {
        groupMembersLoaderManager.startOrContinue()
    }

    fun cancelLoad() {
        groupMembersLoaderManager.cancel()
    }

    fun reload() {
        groupMembersLoaderManager.reload()
    }

    fun pauseLoad() {
        groupMembersLoaderManager.pause()
    }

    private fun observer() {
        observeLoadStatus()
        observeGroups()
    }

    private fun observeLoadStatus() {
        groupMembersLoaderManager.observeLoadStatus()
            .observeOnUI()
            .subscribe(
                {
                    Timber.d("groupMembersLoader status: $it")
                    when (it) {
                        GroupMembersLoaderManager.Status.Initial -> onInitialStatus()
                        GroupMembersLoaderManager.Status.Load -> onLoadStatus()
                        GroupMembersLoaderManager.Status.Pause -> onPauseStatus()
                        GroupMembersLoaderManager.Status.Finish -> onFinishStatus()
                        GroupMembersLoaderManager.Status.Error.RateLimitReached -> {
                            errorText.set(resourceManager.getString(R.string.fragment_groups_error_text_no_access_to_group_members))
                            onErrorStatus()
                        }
                        is GroupMembersLoaderManager.Status.Error.Generic -> {
                            errorText.set(resourceManager.getString(R.string.fragment_groups_error_text_unknown_error))
                            onErrorStatus()
                        }
                    }
                },
                Timber::e
            )
            .addTo(disposables)
    }

    private fun onPauseStatus() {
        startButtonVisible.set(false)
        cancelButtonVisible.set(true)
        reloadButtonVisible.set(false)
        continueButtonVisible.set(true)
        pauseButtonVisible.set(false)
        errorTextVisible.set(false)
    }

    private fun onFinishStatus() {
        startButtonVisible.set(false)
        cancelButtonVisible.set(false)
        reloadButtonVisible.set(true)
        continueButtonVisible.set(false)
        pauseButtonVisible.set(false)
        errorTextVisible.set(false)
    }

    private fun onLoadStatus() {
        startButtonVisible.set(false)
        cancelButtonVisible.set(true)
        reloadButtonVisible.set(false)
        continueButtonVisible.set(false)
        pauseButtonVisible.set(true)
        errorTextVisible.set(false)
    }

    private fun onErrorStatus() {
        startButtonVisible.set(false)
        cancelButtonVisible.set(true)
        reloadButtonVisible.set(false)
        continueButtonVisible.set(true)
        pauseButtonVisible.set(false)
        errorTextVisible.set(true)
    }

    private fun onInitialStatus() {
        startButtonVisible.set(true)
        cancelButtonVisible.set(false)
        reloadButtonVisible.set(false)
        continueButtonVisible.set(false)
        pauseButtonVisible.set(false)
        errorTextVisible.set(false)
    }

    private fun observeGroups() {
        localGroupsRepo.observeGroups()
            .throttleLast(GROUPS_UPDATE_INTERVAL_SECONDS, TimeUnit.SECONDS)
            .observeOnUI()
            .subscribe(
                { groups ->
                    membersCount.set(groups.sumBy { it.membersCount })
                    loadedMembersCount.set(groups.sumBy { it.loadedMembersCount })
                    buttonsVisible.set(true)
                    groupViewModels.clear()
                    groupViewModels.addAll(
                        groups.sortedBy { it.membersCount }.map { group ->
                            GroupViewModel(
                                name = group.name,
                                photo = group.photo,
                                membersCount = group.membersCount,
                                loadedMembersCount = group.loadedMembersCount,
                                hasAccessToMembers = group.hasAccessToMembers
                            )
                        }
                    )
                },
                {
                    errorHandler.handle(it, ::observeGroups)
                }
            )
            .addTo(disposables)
    }

    override fun onCleared() {
        disposables.clear()
    }

    companion object {
        private const val GROUPS_UPDATE_INTERVAL_SECONDS = 1L
    }
}
