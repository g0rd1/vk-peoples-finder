package ru.g0rd1.peoplesfinder.ui.results

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import ru.g0rd1.peoplesfinder.R
import ru.g0rd1.peoplesfinder.base.error.Error
import ru.g0rd1.peoplesfinder.common.ResourceManager
import ru.g0rd1.peoplesfinder.control.groupmembersloader.GroupMembersLoaderManager
import ru.g0rd1.peoplesfinder.repo.access.VKAccessRepo
import ru.g0rd1.peoplesfinder.repo.user.local.LocalUsersRepo
import ru.g0rd1.peoplesfinder.util.observeOnUI
import ru.g0rd1.peoplesfinder.util.subscribeOnIo
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ResultsViewModel @Inject constructor(
    private val localUserRepo: LocalUsersRepo,
    private val errorHandler: Error.Handler,
    private val groupMembersLoaderManager: GroupMembersLoaderManager,
    private val resourceManager: ResourceManager,
    private val vkAccessRepo: VKAccessRepo
) : ViewModel() {

    val showLoader = ObservableBoolean(false)

    val noResultMessage = ObservableField("")

    private val disposables: CompositeDisposable = CompositeDisposable()

    val userViewModels: ObservableList<ResultViewModel> = ObservableArrayList()

    private val updateResultsSubject = PublishSubject.create<Unit>()

    fun onStart() {
        observe()
        updateResultsSubject.onNext(Unit)
            .also { Timber.d("TEST: updateResultsSubject.onNext(Unit)") }
        startUpdatesResults()
    }

    fun refresh() {
        updateResultsSubject.onNext(Unit)
            .also { Timber.d("TEST: updateResultsSubject.onNext(Unit)") }
    }

    private fun observe() {
        observerUpdateResult()
        observeLoadStatus()
    }

    private fun observerUpdateResult() {
//        updateResultsSubject
//            .throttleLatest(1000, TimeUnit.MILLISECONDS)
//            .doOnEach {
//                Timber.d("TEST: doOnEach before throttle")
//                if (userViewModels.isEmpty()) {
//                    showLoader.set(true).also { Timber.d("TEST: showLoader true") }
//                }
//            }
//            .flatMapSingle { localUserRepo.getWithSameGroupsCount() }
//            .observeOnUI()
//            .subscribe(
//                { usersToSameGroupCount ->
//                    userViewModels.clear()
//                    userViewModels.addAll(
//                        usersToSameGroupCount.map { (user, sameGroupCount) ->
//                            ResultViewModel(user, sameGroupCount)
//                        }.filterNot { it.user.id == vkAccessRepo.getUserId() }
//                    )
//                    showLoader.set(false).also { Timber.d("TEST: showLoader false") }
//                },
//                {
//                    showLoader.set(false).also { Timber.d("TEST: showLoader false") }
//                    Timber.e(it)
//                }
//            )
//            .addTo(disposables)
    }

    private fun observeLoadStatus() {
        groupMembersLoaderManager.observeLoadStatus()
            .observeOnUI()
            .subscribe(
                {
                    val noResultsMessage: String =
                        when (it) {
                            GroupMembersLoaderManager.Status.Initial -> {
                                resourceManager.getString(R.string.fragment_results_no_results_load_not_started)
                            }
                            GroupMembersLoaderManager.Status.Finish -> {
                                resourceManager.getString(R.string.fragment_results_no_results_load_finished)
                            }
                            else -> {
                                resourceManager.getString(R.string.fragment_results_no_results_load_started)
                            }
                        }
                    this.noResultMessage.set(noResultsMessage)
                },
                Timber::e
            )
            .addTo(disposables)
    }

    private fun startUpdatesResults() {
        Observable.interval(10, TimeUnit.SECONDS)
            .subscribeOnIo()
            .observeOnUI()
            .subscribe(
                {
                    updateResultsSubject.onNext(Unit)
                        .also { Timber.d("TEST: updateResultsSubject.onNext(Unit)") }
                },
                Timber::e
            )
            .addTo(disposables)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

}