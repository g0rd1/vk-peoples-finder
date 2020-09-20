package ru.g0rd1.peoplesfinder.ui.groups

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import ru.g0rd1.peoplesfinder.R
import ru.g0rd1.peoplesfinder.base.scheduler.Schedulers
import ru.g0rd1.peoplesfinder.control.groupmembersloader.GroupMembersLoader
import ru.planeta.tv.refactor.base.util.ResourcesUtil
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

class GroupsMembersSequentialLoader(
    private val loaders: List<GroupMembersLoader>,
    private val groupsView: GroupsView,
    private val schedulers: Schedulers
) {

    private var startOrPauseButtonAction: StartOrPauseButtonAction =
        StartOrPauseButtonAction.START_DOWNLOAD
        set(value) {
            field = value
            onStartOrPauseButtonActionChanged()
        }

    private val statusesSubject: Subject<Pair<DownloadProgressStatus, DownloadStatus>> =
        BehaviorSubject.create()

    private val loadersStatusesSubject: Subject<GroupMembersLoader.Status> =
        BehaviorSubject.create()

    private val disposables: CompositeDisposable = CompositeDisposable()

    private var downloadProgressStatus: DownloadProgressStatus = DownloadProgressStatus.NOTHING
        set(value) {
            field = value
            Timber.d("TEST! DownloadProgressStatus $value ${Arrays.toString(Thread.currentThread().stackTrace)}")
            statusesSubject.onNext(Pair(downloadProgressStatus, downloadStatus))
        }

    private var downloadStatus: DownloadStatus = DownloadStatus.NOT_LOAD
        set(value) {
            field = value
            Timber.d("TEST! DownloadStatus $value ${Arrays.toString(Thread.currentThread().stackTrace)}")
            statusesSubject.onNext(Pair(downloadProgressStatus, downloadStatus))
        }

    private val listener: (status: GroupMembersLoader.Status) -> Unit = listener@{ status ->
        loadersStatusesSubject.onNext(status)
    }

    init {
        loaders.forEach {
            it.addOnStatusChangeListener(listener)
        }
        if (loaders.any { it.status == GroupMembersLoader.Status.LOAD } && loaders.any { it.status == GroupMembersLoader.Status.PAUSED }) {
            throw IllegalStateException("Не должно быть одна одновременно группы с статусом загрузки и паузы")
        }
        observeStatusChanges()
        observeLoadersStatusesChanges()
        updateStatuses()
    }

    fun onStartOrPauseClicked() {
        when (startOrPauseButtonAction) {
            StartOrPauseButtonAction.START_DOWNLOAD -> {
                load()
            }
            StartOrPauseButtonAction.PAUSE_DOWNLOAD -> {
                pause()
            }
            StartOrPauseButtonAction.CONTINUE_DOWNLOAD -> {
                load()
            }
            StartOrPauseButtonAction.DOWNLOAD_AGAIN -> {
                stop()
                load()
            }
        }
    }

    fun onStopButtonClicked() {
        if (downloadStatus != DownloadStatus.LOAD) {
            throw IllegalStateException("downloadStatus must be ${DownloadStatus.LOAD} when click stop button")
        }
        stop()
    }

    fun clear() {
        disposables.clear()
    }

    private fun onStartOrPauseButtonActionChanged() {
        updateGroupsView()
    }

    private fun updateGroupsView() {
        groupsView.startOrPauseButtonText = startOrPauseButtonAction.actionName
        when (startOrPauseButtonAction) {
            StartOrPauseButtonAction.START_DOWNLOAD -> {
                groupsView.showStopButton = false
            }
            StartOrPauseButtonAction.PAUSE_DOWNLOAD -> {
                groupsView.showStopButton = true
            }
            StartOrPauseButtonAction.CONTINUE_DOWNLOAD -> {
                groupsView.showStopButton = true
            }
            StartOrPauseButtonAction.DOWNLOAD_AGAIN -> {
                groupsView.showStopButton = false
            }
        }
    }

    private fun observeStatusChanges() {
        statusesSubject
            .distinctUntilChanged()
            .throttleLatest(STATUS_CHANGE_THROTTLE_SECONDS, TimeUnit.SECONDS)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.main())
            .subscribe { (downloadProgressStatus, downloadStatus) ->
                onStatusChanged(downloadProgressStatus, downloadStatus)
            }
            .addTo(disposables)
    }

    private fun observeLoadersStatusesChanges() {
        loadersStatusesSubject
            .throttleLatest(LOADERS_STATUSES_CHANGE_THROTTLE_SECONDS, TimeUnit.SECONDS)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.main())
            .subscribe { status ->
                if (status == GroupMembersLoader.Status.FINISH) {
                    load()
                }
                updateStatuses()
            }
            .addTo(disposables)
    }

    private fun updateStatuses() {
        updateDownloadProgressStatus()
        updateDownloadStatus()
    }

    private fun updateDownloadProgressStatus() {
        downloadProgressStatus = when {
            loaders.all { it.status == GroupMembersLoader.Status.FINISH } -> {
                DownloadProgressStatus.ALL
            }
            loaders.any { it.loadedMembersCount > 0 } -> {
                DownloadProgressStatus.SOME
            }
            else -> {
                DownloadProgressStatus.NOTHING
            }
        }
    }

    private fun updateDownloadStatus() {
        downloadStatus = if (loaders.any { it.status == GroupMembersLoader.Status.LOAD }) {
            DownloadStatus.LOAD
        } else {
            DownloadStatus.NOT_LOAD
        }
    }

    @Synchronized
    private fun pause() {
        loaders.forEach { it.pause() }
    }

    @Synchronized
    private fun load() {
        var counter = 0
        run cycle@{
            loaders.forEach {
                if (it.status == GroupMembersLoader.Status.LOAD) {
                    counter++
                } else if (it.status != GroupMembersLoader.Status.FINISH) {
                    Timber.d("start loader ${it.getGroupId()}")
                    it.start()
                    counter++
                }
                if (counter >= SYNCHRONOUS_DOWNLOAD_MAX_COUNT) {
                    return@cycle
                }
                Timber.d("counter: $counter")
            }
        }
    }

    @Synchronized
    private fun stop() {
        loaders.forEach { it.stop() }
    }

    @Synchronized
    private fun onStatusChanged(
        downloadProgressStatus: DownloadProgressStatus,
        downloadStatus: DownloadStatus
    ) {
        // if (downloadProgressStatus == DownloadProgressStatus.ALL) {
        //     if (downloadStatus != DownloadStatus.IDLE) {
        //         this.downloadStatus = DownloadStatus.IDLE
        //         return
        //     }
        // }
        startOrPauseButtonAction =
            if (downloadProgressStatus == DownloadProgressStatus.ALL && downloadStatus == DownloadStatus.NOT_LOAD) {
                StartOrPauseButtonAction.DOWNLOAD_AGAIN
            } else if (downloadProgressStatus == DownloadProgressStatus.SOME && downloadStatus == DownloadStatus.NOT_LOAD) {
                StartOrPauseButtonAction.CONTINUE_DOWNLOAD
            } else if (downloadProgressStatus == DownloadProgressStatus.NOTHING && downloadStatus == DownloadStatus.NOT_LOAD) {
                StartOrPauseButtonAction.START_DOWNLOAD
            } else if (downloadStatus == DownloadStatus.LOAD) {
                StartOrPauseButtonAction.PAUSE_DOWNLOAD
            } else {
                throw IllegalStateException("Недопустимое сочетание downloadProgressStatus == $downloadProgressStatus и downloadStatus == $downloadStatus")
            }
    }

    private enum class StartOrPauseButtonAction(val actionName: String) {
        START_DOWNLOAD(ResourcesUtil.Strings.get(R.string.fragment_groups_download_button_start_download)),
        PAUSE_DOWNLOAD(ResourcesUtil.Strings.get(R.string.fragment_groups_download_button_pause_download)),
        CONTINUE_DOWNLOAD(ResourcesUtil.Strings.get(R.string.fragment_groups_download_button_continue_download)),
        DOWNLOAD_AGAIN(ResourcesUtil.Strings.get(R.string.fragment_groups_download_button_download_again))
    }

    private enum class DownloadProgressStatus {
        NOTHING, SOME, ALL
    }

    private enum class DownloadStatus {
        LOAD, NOT_LOAD
    }

    companion object {
        private const val STATUS_CHANGE_THROTTLE_SECONDS = 1L
        private const val LOADERS_STATUSES_CHANGE_THROTTLE_SECONDS = 1L
        private const val SYNCHRONOUS_DOWNLOAD_MAX_COUNT = 3
    }

}