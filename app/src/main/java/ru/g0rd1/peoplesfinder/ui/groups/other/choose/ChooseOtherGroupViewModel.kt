package ru.g0rd1.peoplesfinder.ui.groups.other.choose

import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Completable
import io.reactivex.Single
import ru.g0rd1.peoplesfinder.R
import ru.g0rd1.peoplesfinder.base.SnackbarManager
import ru.g0rd1.peoplesfinder.base.global.SingleLiveEvent
import ru.g0rd1.peoplesfinder.common.ResourceManager
import ru.g0rd1.peoplesfinder.model.Group
import ru.g0rd1.peoplesfinder.model.VkResult
import ru.g0rd1.peoplesfinder.repo.group.local.LocalGroupsRepo
import ru.g0rd1.peoplesfinder.repo.group.vk.VkGroupsRepo
import ru.g0rd1.peoplesfinder.ui.choose.single.SingleChooseItemViewData
import ru.g0rd1.peoplesfinder.ui.choose.single.SingleChooseViewModel
import ru.g0rd1.peoplesfinder.util.exhaustive
import ru.g0rd1.peoplesfinder.util.observeOnUI
import ru.g0rd1.peoplesfinder.util.subscribeOnIo
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ChooseOtherGroupViewModel @Inject constructor(
    private val vkGroupsRepo: VkGroupsRepo,
    private val localGroupsRepo: LocalGroupsRepo,
    resourceManager: ResourceManager,
    private val snackbarManager: SnackbarManager,
) : SingleChooseViewModel<Group>() {

    override val title: String = resourceManager.getString(R.string.choose_other_group_dialog_title)
    override val searchTextHint: String =
        resourceManager.getString(R.string.choose_other_group_dialog_search_text_hint)

    val closeEvent = SingleLiveEvent<Unit>()

    val showSnackbar = SingleLiveEvent<String>()

    override val cancelChoiceVisible: Boolean = false

    override fun onStart() {
        super.onStart()
        observe()
    }

    private fun observe() {
        observeSearchText()
            .doOnNext { loaderVisible.set(it.isNotBlank()) }
            .filter { it.isNotBlank() }
            .flatMapSingle { vkGroupsRepo.searchGroups(it) }
            .subscribeOnIo()
            .observeOnUI()
            .subscribe(
                {
                    when (it) {
                        is VkResult.Error.ApiVk -> {
                            loaderVisible.set(false)
                            setError(it.error.message)
                            items.set(listOf())
                        }
                        is VkResult.Error.Generic -> {
                            loaderVisible.set(false)
                            // TODO заменить на строковый ресурс
                            setError("Не удалось получить список групп.\nПопробуйте выбрать город позже.")
                            items.set(listOf())
                        }
                        is VkResult.Success -> {
                            loaderVisible.set(false)
                            clearError()
                            items.set(
                                it.data.map { group ->
                                    SingleChooseItemViewData(
                                        data = group,
                                        name = group.name,
                                        id = group.id,
                                        imageUrl = group.photo
                                    )
                                }
                            )
                        }
                    }.exhaustive
                },
                Timber::e
            ).disposeLater()
    }

    override fun onItemClick(item: SingleChooseItemViewData<Group>) {
        Single.zip(
            localGroupsRepo.observeUserGroups().firstOrError(),
            localGroupsRepo.observeOtherGroups().firstOrError()
        ) { userGroups, otherGroups -> userGroups to otherGroups }
            .flatMapCompletable { (userGroups, otherGroups) ->
                if (userGroups.any { it.id == item.data.id }) {
                    return@flatMapCompletable Completable.complete()
                        .observeOnUI()
                        .andThen(
                            Completable.fromAction {
                                showSnackbar.value = "Группа находится в разделе \"Другие группы\""
                            }
                        )
                }
                if (otherGroups.any { it.id == item.data.id }) {
                    return@flatMapCompletable Completable.complete()
                        .observeOnUI()
                        .andThen(
                            Completable.fromAction {
                                showSnackbar.value = "Группа уже добавлена"
                            }
                        )
                }
                localGroupsRepo.insert(listOf(item.data))
                    .observeOnUI()
                    .andThen(
                        Completable.fromAction {
                            snackbarManager.showSnackbar("Группа \"${item.name}\" успешно добавлена", Snackbar.LENGTH_LONG)
                            closeEvent.call()
                        }
                    )
            }
            .subscribe(
                { },
                {
                    Timber.e(it)
                    showSnackbar.value = "Произошла ошибка при добавлении группы"
                }
            )
            .disposeLater()
    }

    override fun cancelChoice() = Unit

    override fun close() {
        closeEvent.call()
    }

}