package ru.g0rd1.peoplesfinder.ui.groups

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.g0rd1.peoplesfinder.R
import ru.g0rd1.peoplesfinder.base.BaseViewModel
import ru.g0rd1.peoplesfinder.base.error.Error
import ru.g0rd1.peoplesfinder.common.ResourceManager
import ru.g0rd1.peoplesfinder.control.groupmembersloader.GroupMembersLoaderManager
import ru.g0rd1.peoplesfinder.repo.group.local.LocalGroupsRepo
import ru.g0rd1.peoplesfinder.util.observeOnUI
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class GroupsViewModel @Inject constructor(
    private val groupMembersLoaderManager: GroupMembersLoaderManager,
    private val localGroupsRepo: LocalGroupsRepo,
    private val errorHandler: Error.Handler,
    private val resourceManager: ResourceManager
) : BaseViewModel() {

    val showContent = ObservableBoolean(false)
    val showLoader = ObservableBoolean(false)

    val cancelButtonVisible = ObservableBoolean(false)
    val startButtonVisible = ObservableBoolean(false)
    val reloadButtonVisible = ObservableBoolean(false)
    val continueButtonVisible = ObservableBoolean(false)
    val pauseButtonVisible = ObservableBoolean(false)

    val commandProcessing = ObservableBoolean(false)
    val showCommandProcessingLoader = ObservableBoolean(false)

    val errorTextVisible = ObservableBoolean(false)
    val errorText: ObservableField<String> = ObservableField("")

    val membersCount = ObservableInt(0)
    val loadedMembersCount = ObservableInt(0)

    val groupViewModels: ObservableField<List<GroupViewData>> = ObservableField()

    override fun onStart() {
        observe()
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

    private fun observe() {
        observeLoadStatus()
        observeGroups()
    }

    private fun observeLoadStatus() {
        groupMembersLoaderManager.observeLoadStatus()
            .observeOnUI()
            .subscribe(
                {
                    setCommandProcessingLoaderVisibility(it is GroupMembersLoaderManager.Status.CommandProcessing)
                    when (it) {
                        GroupMembersLoaderManager.Status.Initial -> onInitialStatus()
                        GroupMembersLoaderManager.Status.Load -> onLoadStatus()
                        GroupMembersLoaderManager.Status.Pause -> onPauseStatus()
                        GroupMembersLoaderManager.Status.Finish -> onFinishStatus()
                        GroupMembersLoaderManager.Status.Error.RateLimitReached -> onRateLimitReachedError()
                        is GroupMembersLoaderManager.Status.Error.Generic -> onGenericError()
                        GroupMembersLoaderManager.Status.CommandProcessing -> onCommandProcessing()
                    }
                },
                Timber::e
            ).disposeLater()
    }

    private var changeProcessingLoaderVisibilityDisposable: Disposable? = null

    private fun setCommandProcessingLoaderVisibility(visible: Boolean) {
        changeProcessingLoaderVisibilityDisposable?.dispose()
        if (!visible) {
            showCommandProcessingLoader.set(false)
            return
        }
        Single.timer(500, TimeUnit.MILLISECONDS, Schedulers.newThread())
            .observeOnUI()
            .subscribe(
                { showCommandProcessingLoader.set(true) },
                Timber::e
            )
            .also { changeProcessingLoaderVisibilityDisposable = it }
    }

    private fun onCommandProcessing() {
        commandProcessing.set(true)
    }

    private fun onPauseStatus() {
        startButtonVisible.set(false)
        cancelButtonVisible.set(true)
        reloadButtonVisible.set(false)
        continueButtonVisible.set(true)
        pauseButtonVisible.set(false)
        errorTextVisible.set(false)
        commandProcessing.set(false)
    }

    private fun onFinishStatus() {
        startButtonVisible.set(false)
        cancelButtonVisible.set(false)
        reloadButtonVisible.set(true)
        continueButtonVisible.set(false)
        pauseButtonVisible.set(false)
        errorTextVisible.set(false)
        commandProcessing.set(false)
    }

    private fun onLoadStatus() {
        startButtonVisible.set(false)
        cancelButtonVisible.set(true)
        reloadButtonVisible.set(false)
        continueButtonVisible.set(false)
        pauseButtonVisible.set(true)
        errorTextVisible.set(false)
        commandProcessing.set(false)
    }

    private fun onRateLimitReachedError() {
        errorText.set(resourceManager.getString(R.string.fragment_groups_error_text_no_access_to_group_members))
        onErrorStatus()
    }

    private fun onGenericError() {
        errorText.set(resourceManager.getString(R.string.fragment_groups_error_text_unknown_error))
        onErrorStatus()
    }

    private fun onErrorStatus() {
        startButtonVisible.set(false)
        cancelButtonVisible.set(true)
        reloadButtonVisible.set(false)
        continueButtonVisible.set(true)
        pauseButtonVisible.set(false)
        errorTextVisible.set(true)
        commandProcessing.set(false)
    }

    private fun onInitialStatus() {
        startButtonVisible.set(true)
        cancelButtonVisible.set(false)
        reloadButtonVisible.set(false)
        continueButtonVisible.set(false)
        pauseButtonVisible.set(false)
        errorTextVisible.set(false)
        commandProcessing.set(false)
    }

    private fun observeGroups() {
        showLoader.set(true)
        showContent.set(false)
        localGroupsRepo.observeGroups()
            .observeOnUI()
            .subscribe(
                { groups ->
                    membersCount.set(groups.sumOf { it.membersCount })
                    loadedMembersCount.set(groups.sumOf{ it.loadedMembersCount })
                    showContent.set(true)
                    groupViewModels.set(
                        groups.sortedBy { it.membersCount }.map { group ->
                            GroupViewData(
                                id = group.id,
                                name = group.name,
                                photo = group.photo,
                                membersCount = group.membersCount,
                                loadedMembersCount = group.loadedMembersCount,
                                hasAccessToMembers = group.hasAccessToMembers
                            )
                        }
                    )
                    showLoader.set(false)
                    showContent.set(true)
                },
                {
                    showLoader.set(false)
                    showContent.set(false)
                    errorHandler.handle(it, ::observeGroups)
                }
            ).disposeLater()
    }

}
