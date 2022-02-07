package ru.g0rd1.peoplesfinder.ui.userDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.Completable
import io.reactivex.Single
import ru.g0rd1.peoplesfinder.R
import ru.g0rd1.peoplesfinder.base.navigator.AppNavigator
import ru.g0rd1.peoplesfinder.common.ResourceManager
import ru.g0rd1.peoplesfinder.model.Optional
import ru.g0rd1.peoplesfinder.repo.group.local.LocalGroupsRepo
import ru.g0rd1.peoplesfinder.repo.user.local.LocalUsersRepo
import ru.g0rd1.peoplesfinder.util.observeOnUI

class UserDetailSingleViewModel @AssistedInject constructor(
    @Assisted private val userId: Int,
    private val localUsersRepo: LocalUsersRepo,
    localGroupsRepo: LocalGroupsRepo,
    private val resourceManager: ResourceManager,
    appNavigator: AppNavigator,
): UserDetailViewModel(
    localUsersRepo = localUsersRepo,
    localGroupsRepo = localGroupsRepo,
    resourceManager = resourceManager,
    appNavigator = appNavigator,
) {
    override fun previousUser() = Unit

    override fun nextUser() = Unit

    override fun onUserChanged(): Completable {
        return Completable.fromAction { getUser() }
    }

    override fun onStart() {
        getUser()
    }

    private fun getUser() {
        localUsersRepo.getById(userId)
            .flatMap { optionalUser ->
                when (optionalUser) {
                    is Optional.Empty -> Single.just(Optional.empty())
                    is Optional.Value -> {
                        val user = optionalUser.value
                        getSameGroupsAndTypes(user.id).map { (sameGroups, userTypes) ->
                            Optional.create(Triple(user, sameGroups, userTypes))
                        }
                    }
                }
            }
            .observeOnUI()
            .doOnSubscribe {
                infoVisible.set(false)
                loadingVisible.set(true)
                errorVisible.set(false)
            }
            .subscribe(
                { optionalInfo ->
                    when (optionalInfo) {
                        is Optional.Empty -> {
                            showError(resourceManager.getString(R.string.unknown_error)) {
                                getUser()
                            }
                        }
                        is Optional.Value -> {
                            val (user, sameGroups, userTypes) = optionalInfo.value
                            setUserInfo(user, sameGroups, userTypes)
                        }
                    }
                },
                {
                    showError(resourceManager.getString(R.string.unknown_error)) { getUser() }
                }
            ).disposeLater()
    }

    @AssistedFactory
    interface Factory {
        fun create(userId: Int): UserDetailSingleViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            userId: Int,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(userId) as T
            }
        }
    }

}