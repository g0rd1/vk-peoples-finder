package ru.g0rd1.peoplesfinder.ui.settings.group

import dagger.hilt.android.lifecycle.HiltViewModel
import ru.g0rd1.peoplesfinder.R
import ru.g0rd1.peoplesfinder.base.global.SingleLiveEvent
import ru.g0rd1.peoplesfinder.common.ResourceManager
import ru.g0rd1.peoplesfinder.model.Group
import ru.g0rd1.peoplesfinder.repo.filters.FiltersRepo
import ru.g0rd1.peoplesfinder.repo.group.local.LocalGroupsRepo
import ru.g0rd1.peoplesfinder.ui.choose.multi.MultichooseItemViewData
import ru.g0rd1.peoplesfinder.ui.choose.multi.MultichooseViewModel
import ru.g0rd1.peoplesfinder.util.observeOnUI
import ru.g0rd1.peoplesfinder.util.subscribeOnIo
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ChooseRequiredGroupsViewModel @Inject constructor(
    private val filtersRepo: FiltersRepo,
    private val groupsRepo: LocalGroupsRepo,
    resourceManager: ResourceManager,
) : MultichooseViewModel<Group>() {

    override val title: String = resourceManager.getString(R.string.choose_required_groups_dialog_title)
    override val searchTextHint: String =
        resourceManager.getString(R.string.choose_required_groups_dialog_search_text_hint)

    val closeEvent = SingleLiveEvent<Unit>()

    override fun onStart() {
        super.onStart()
        observe()
    }

    private fun observe() {
        observeSearchText()
            .doOnEach { loaderVisible.set(true) }
            .flatMapSingle { searchText ->
                groupsRepo.get().map {  groups ->
                    groups.filter { it.name.contains(searchText, ignoreCase = true) }
                }
            }
            .subscribeOnIo()
            .observeOnUI()
            .subscribe(
                { groups ->
                    val requiredGroups = filtersRepo.getFilterParameters().requiredGroupIds
                    loaderVisible.set(false)
                    clearError()
                    items.set(
                        groups.map { group ->
                            MultichooseItemViewData(
                                data = group,
                                name = group.name,
                                id = group.id,
                                choosed = requiredGroups.any { it == group.id },
                                imageUrl = group.photo
                            )
                        }
                    )
                },
                Timber::e
            ).disposeLater()
    }

    override fun onItemClick(item: MultichooseItemViewData<Group>) {
        items.set(
            items.get()?.map {
                if (it.id == item.id) it.copy(choosed = !item.choosed) else it
            }
        )
    }

    override fun dropChoice() {
        items.set(items.get()?.map { it.copy(choosed = false) })
    }

    override fun confirmChoice() {
        filtersRepo.getFilterParameters().requiredGroupIds
        filtersRepo.setRequiredGroupIds(
            items.get()?.filter { it.choosed }?.map { (it.data as Group).id } ?: listOf()
        )
        closeEvent.call()
    }

    override fun close() {
        closeEvent.call()
    }

}