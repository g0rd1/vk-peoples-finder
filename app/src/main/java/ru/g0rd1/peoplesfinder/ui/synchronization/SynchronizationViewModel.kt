package ru.g0rd1.peoplesfinder.ui.synchronization

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Completable
import ru.g0rd1.peoplesfinder.base.BaseViewModel
import ru.g0rd1.peoplesfinder.base.navigator.AppNavigator
import ru.g0rd1.peoplesfinder.model.Group
import ru.g0rd1.peoplesfinder.model.VkResult
import ru.g0rd1.peoplesfinder.repo.group.local.LocalGroupsRepo
import ru.g0rd1.peoplesfinder.repo.group.vk.VkGroupsRepo
import ru.g0rd1.peoplesfinder.util.exhaustive
import ru.g0rd1.peoplesfinder.util.observeOnUI
import javax.inject.Inject

@HiltViewModel
class SynchronizationViewModel @Inject constructor(
    private val localGroupsRepo: LocalGroupsRepo,
    private val vkGroupsRepo: VkGroupsRepo,
    private val synchronizationObserver: SynchronizationObserver,
    private val appNavigator: AppNavigator
) : BaseViewModel() {

    val showError = ObservableBoolean()
    val errorText = ObservableField<String>()

    val showLoading = ObservableBoolean()

    override fun onStart() {
        synchronize()
    }

    fun retry() {
        synchronize()
    }

    private fun synchronize() {
        showLoading.set(true)
        vkGroupsRepo.getGroups()
            .flatMapCompletable { groupsResult ->
                when (groupsResult) {
                    is VkResult.Error.ApiVk -> Completable.fromAction { handleError() }
                    is VkResult.Error.Generic -> Completable.fromAction { handleError() }
                    is VkResult.Success -> getSyncGroupsWithRepoCompletable(groupsResult.data)
                }.exhaustive
            }
            .observeOnUI()

            .subscribe(
                {
                    synchronizationObserver.synchronized()
                    appNavigator.groups()
                },
                {
                    handleError()
                }
            )
            .disposeLater()
    }

    private fun getSyncGroupsWithRepoCompletable(groups: List<Group>): Completable {
        return localGroupsRepo.insert(groups)
            .andThen(localGroupsRepo.deleteUserGroupsNotIn(groups.map { it.id }))
            .andThen(
                Completable.concat(
                    groups.mapIndexed { index, group ->
                        localGroupsRepo.updateSequentialNumber(group.id, index)
                    }
                )
            )
    }

    private fun handleError() {
        showLoading.set(false)
        showError.set(true)
        errorText.set("Ошибка синхронизации с серверами Вконтакте")
    }

}