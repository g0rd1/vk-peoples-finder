package ru.g0rd1.peoplesfinder.ui.userDetail

import dagger.hilt.android.lifecycle.HiltViewModel
import ru.g0rd1.peoplesfinder.base.navigator.AppNavigator
import ru.g0rd1.peoplesfinder.common.ResourceManager
import ru.g0rd1.peoplesfinder.repo.group.local.LocalGroupsRepo
import ru.g0rd1.peoplesfinder.repo.user.local.LocalUsersRepo
import ru.g0rd1.peoplesfinder.ui.userDetail.fsm.UserDetailMultipleFeature
import javax.inject.Inject

@HiltViewModel
class UserDetailMultipleViewModel @Inject constructor(
    private val localUsersRepo: LocalUsersRepo,
    private val localGroupsRepo: LocalGroupsRepo,
    private val resourceManager: ResourceManager,
    private val appNavigator: AppNavigator,
    private val userDetailMultipleFeature: UserDetailMultipleFeature,
) : UserDetailViewModel(
    localUsersRepo = localUsersRepo,
    localGroupsRepo = localGroupsRepo,
    resourceManager = resourceManager,
    appNavigator = appNavigator,
) {

    override fun onStart() {
        // onWithSwitchesUserDetailDialogType()
        // userPagingSource.
        observe()
    }

    private fun observe() {
        observeState()
    }

    private fun observeState() {
        userDetailMultipleFeature.also { it.disposeLater() }
        // .subscribe()
    }

    // private fun onWithSwitchesUserDetailDialogType() {
    //     localUsersRepo.getUserWithMaxSameGroupsCountWithFilters(listOf(UserType.BLOCKED/*, UserType.VIEWED*/))
    //         .flatMap { userWithMaxGroupsCountResult ->
    //             when (userWithMaxGroupsCountResult) {
    //                 UserWithSameGroupsCountResult.Empty -> {
    //                     // TODO Сделать запрос групп, чтобы понять если просмотренный группы соответсвующие фильтрам
    //                     Single.just(Optional.empty())
    //                 }
    //                 is UserWithSameGroupsCountResult.Result -> {
    //                     val user = userWithMaxGroupsCountResult.user
    //                     getSameGroupsAndTypes(user.id).map { (sameGroups, userTypes) ->
    //                         Optional.create(Triple(user, sameGroups, userTypes))
    //                     }
    //                 }
    //             }
    //         }
    //         .observeOnUI()
    //         .doOnSubscribe {
    //             infoVisible.set(false)
    //             loadingVisible.set(true)
    //             errorVisible.set(false)
    //         }
    //         .subscribe(
    //             { optionalInfo ->
    //                 when (optionalInfo) {
    //                     is Optional.Empty -> {
    //                         // TODO Выдавать сообщение о том что результатов нет
    //                     }
    //                     is Optional.Value -> {
    //                         val (user, sameGroups, userTypes) = optionalInfo.value
    //                         setUserInfo(user, sameGroups, userTypes)
    //                         infoVisible.set(true)
    //                         loadingVisible.set(false)
    //                         errorVisible.set(false)
    //                     }
    //                 }
    //             },
    //             {
    //                 showError(resourceManager.getString(R.string.unknown_error)) { onWithSwitchesUserDetailDialogType() }
    //             }
    //         ).disposeLater()
    // }

}