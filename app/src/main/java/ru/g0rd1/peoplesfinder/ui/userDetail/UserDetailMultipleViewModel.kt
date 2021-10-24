package ru.g0rd1.peoplesfinder.ui.userDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import ru.g0rd1.peoplesfinder.base.navigator.AppNavigator
import ru.g0rd1.peoplesfinder.common.ResourceManager
import ru.g0rd1.peoplesfinder.model.Optional
import ru.g0rd1.peoplesfinder.repo.group.local.LocalGroupsRepo
import ru.g0rd1.peoplesfinder.repo.user.history.UserHistoryRepo
import ru.g0rd1.peoplesfinder.repo.user.local.LocalUsersRepo
import ru.g0rd1.peoplesfinder.ui.userDetail.fsm.UserDetailMultiple
import ru.g0rd1.peoplesfinder.ui.userDetail.fsm.UserDetailMultipleFeature
import ru.g0rd1.peoplesfinder.util.observeOnUI

class UserDetailMultipleViewModel @AssistedInject constructor(
    localUsersRepo: LocalUsersRepo,
    localGroupsRepo: LocalGroupsRepo,
    resourceManager: ResourceManager,
    appNavigator: AppNavigator,
    private val userHistoryRepo: UserHistoryRepo,
    private val userDetailMultipleFeature: UserDetailMultipleFeature,
) : UserDetailViewModel(
    localUsersRepo = localUsersRepo,
    localGroupsRepo = localGroupsRepo,
    resourceManager = resourceManager,
    appNavigator = appNavigator,
) {

    private val userDetailMultipleSubject = BehaviorSubject.create<UserDetailMultiple.State>()

    override fun onStart() {
        loadingVisible.set(true)
        infoVisible.set(false)
        // onWithSwitchesUserDetailDialogType()
        // userPagingSource.
        userDetailMultipleFeature.subscribe(userDetailMultipleSubject)
        observe()
        // userDetailMultipleFeature.loadUsers()
    }

    override fun previousUser() {
        userDetailMultipleFeature.toPrevious()
    }

    override fun nextUser() {
        userDetailMultipleFeature.toNext()
    }

    private fun observe() {
        observeState()
    }

    private fun observeState() {
        userDetailMultipleSubject
            .flatMapSingle { state ->
                if (state.currentUser != null) {
                    userHistoryRepo.insert(state.currentUser.id).andThen(
                        getSameGroupsAndTypes(state.currentUser.id).map { (sameGroups, userTypes) ->
                            Optional.create(Triple(state, sameGroups, userTypes))
                        }
                    )
                } else {
                    Single.just(Optional.empty())
                }
            }
            .observeOnUI()
            .subscribe(
                {
                    when (it) {
                        is Optional.Empty -> Unit // TODO no user show loading
                        is Optional.Value -> {
                            val (state, sameGroups, userTypes) = it.value
                            if (state.currentUser == null) return@subscribe
                            setUserInfo(state.currentUser, sameGroups, userTypes)
                            loadingVisible.set(state.loading)
                            infoVisible.set(!state.loading)
                            showLeftArrow.set(state.userResults.first().user.id != state.currentUser.id)
                            showRightArrow.set(state.userResults.last().user.id != state.currentUser.id)
                        }
                    }
                },
                {
                    showError("Неизвестная ошибка") { observeState() }
                }
            )
            .disposeLater()

        // userDetailMultipleFeature.also { it.disposeLater() }
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

    @AssistedFactory
    interface Factory {
        fun create(): UserDetailMultipleViewModel
    }

    companion object {
        fun provideFactory(assistedFactory: Factory): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return assistedFactory.create() as T
            }
        }
    }

}