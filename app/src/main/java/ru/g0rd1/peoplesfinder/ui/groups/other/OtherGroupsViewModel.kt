package ru.g0rd1.peoplesfinder.ui.groups.other

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.g0rd1.peoplesfinder.base.BaseViewModel
import ru.g0rd1.peoplesfinder.base.error.Error
import ru.g0rd1.peoplesfinder.base.global.SingleLiveEvent
import ru.g0rd1.peoplesfinder.common.ResourceManager
import ru.g0rd1.peoplesfinder.control.groupmembersloader.GroupMembersLoaderManager
import ru.g0rd1.peoplesfinder.repo.group.local.LocalGroupsRepo
import ru.g0rd1.peoplesfinder.ui.groups.GroupViewData
import ru.g0rd1.peoplesfinder.util.observeOnUI
import javax.inject.Inject

@HiltViewModel
class OtherGroupsViewModel @Inject constructor(
    private val groupMembersLoaderManager: GroupMembersLoaderManager,
    private val localGroupsRepo: LocalGroupsRepo,
    private val errorHandler: Error.Handler,
    private val resourceManager: ResourceManager,
) : BaseViewModel() {

    val showLoader = ObservableBoolean(false)
    val showContent = ObservableBoolean(false)
    val groupViewModels: ObservableField<List<GroupViewData>> = ObservableField()

    val showAddGroup: SingleLiveEvent<Unit> = SingleLiveEvent()

    override fun onStart() {
        observeGroups()
    }

    fun addGroup() {
        showAddGroup.call()
    }

    private fun observeGroups() {
        showLoader.set(true)
        showContent.set(false)
        localGroupsRepo.observeOtherGroups()
            .observeOnUI()
            .subscribe(
                { groups ->
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
