package ru.g0rd1.peoplesfinder.ui.synchronization

import androidx.lifecycle.ViewModel
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import ru.g0rd1.peoplesfinder.base.error.ErrorHandler
import ru.g0rd1.peoplesfinder.base.navigator.AppNavigator
import ru.g0rd1.peoplesfinder.repo.group.local.LocalGroupsRepo
import ru.g0rd1.peoplesfinder.repo.group.vk.VkGroupsRepo
import ru.g0rd1.peoplesfinder.util.observeOnUI
import timber.log.Timber
import javax.inject.Inject

class SynchronizationViewModel @Inject constructor(
    private val localGroupsRepo: LocalGroupsRepo,
    private val vkGroupsRepo: VkGroupsRepo,
    private val errorHandler: ErrorHandler,
    private val synchronizationObserver: SynchronizationObserver,
    private val appNavigator: AppNavigator
) : ViewModel() {

    private val disposables = CompositeDisposable()

    fun onStart() {
        Timber.d("TEST SynchronizationViewModel init")
        synchronize()
    }

    private fun synchronize() {
        vkGroupsRepo.getGroups()
            .flatMapCompletable { groups ->

                localGroupsRepo.insert(groups)
                    .andThen(localGroupsRepo.deleteNotIn(groups.map { it.id }))
                    .andThen(
                        Completable.concat(
                            groups.mapIndexed { index, group ->
                                localGroupsRepo.updateSequentialNumber(group.id, index)
                            }
                        )
                    )
            }
            .observeOnUI()
            .subscribe(
                {
                    synchronizationObserver.synchronized()
                    appNavigator.groups()
                },
                {
                    errorHandler.handle(it, ::synchronize)
                }
            )
            .addTo(disposables)
    }


    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}