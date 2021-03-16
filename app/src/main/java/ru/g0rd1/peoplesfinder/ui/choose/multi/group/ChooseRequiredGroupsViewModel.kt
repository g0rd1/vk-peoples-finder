package ru.g0rd1.peoplesfinder.ui.choose.multi.group

import ru.g0rd1.peoplesfinder.base.global.SingleLiveEvent
import ru.g0rd1.peoplesfinder.model.Group
import ru.g0rd1.peoplesfinder.repo.filters.FiltersRepo
import ru.g0rd1.peoplesfinder.repo.group.local.LocalGroupsRepo
import ru.g0rd1.peoplesfinder.ui.choose.multi.MultichooseItemViewModel
import ru.g0rd1.peoplesfinder.ui.choose.multi.MultichooseViewModel
import ru.g0rd1.peoplesfinder.util.observeOnUI
import ru.g0rd1.peoplesfinder.util.subscribeOnIo
import timber.log.Timber
import javax.inject.Inject

class ChooseRequiredGroupsViewModel @Inject constructor(
    private val filtersRepo: FiltersRepo,
    private val groupsRepo: LocalGroupsRepo
) : MultichooseViewModel<Group>() {

    val closeEvent = SingleLiveEvent<Unit>()

    override fun onStart() {
        super.onStart()
        observe()
    }

    private fun observe() {
        observeSearchText()
            .doOnEach { loaderVisible.set(true) }
            .flatMapSingle {
                groupsRepo.get()
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
                            MultichooseItemViewModel(
                                data = group,
                                name = group.name,
                                id = group.id,
                                choosed = requiredGroups.any { it == group.id },
                                imageUrl = null
                            )
                        }
                    )
                },
                Timber::e
            ).disposeLater()
    }

    override fun onItemClick(position: Int) {
        items.set(
            items.get()?.mapIndexed { index, item ->
                if (position == index) item.copy(choosed = !item.choosed) else item
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
    }

}