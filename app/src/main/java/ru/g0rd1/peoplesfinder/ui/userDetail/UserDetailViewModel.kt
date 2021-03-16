package ru.g0rd1.peoplesfinder.ui.userDetail

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.lifecycle.ViewModel
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import ru.g0rd1.peoplesfinder.R
import ru.g0rd1.peoplesfinder.common.ResourceManager
import ru.g0rd1.peoplesfinder.model.Group
import ru.g0rd1.peoplesfinder.model.Optional
import ru.g0rd1.peoplesfinder.model.User
import ru.g0rd1.peoplesfinder.model.UserType
import ru.g0rd1.peoplesfinder.model.result.UserWithMaxGroupsCountResult
import ru.g0rd1.peoplesfinder.repo.group.local.LocalGroupsRepo
import ru.g0rd1.peoplesfinder.repo.user.local.LocalUsersRepo
import ru.g0rd1.peoplesfinder.util.observeOnUI

class UserDetailViewModel @AssistedInject constructor(
    @Assisted private val type: UserDetailDialogType,
    private val localUsersRepo: LocalUsersRepo,
    private val localGroupsRepo: LocalGroupsRepo,
    private val resourceManager: ResourceManager
) : ViewModel() {

    val arrowVisible = ObservableBoolean(false)

    val infoVisible = ObservableBoolean(false)
    val loadingVisible = ObservableBoolean(false)
    val errorVisible = ObservableBoolean(false)

    val errorText = ObservableField<String>()
    val retryFunction = ObservableField<() -> Unit>()

    val userName = ObservableField<String>()
    val userAge = ObservableField<String>()
    val userSex = ObservableField<String>()
    val userRelation = ObservableField<String>()
    val userCountry = ObservableField<String>()
    val userCity = ObservableField<String>()

    val isViewed = ObservableBoolean(false)
    val isBlocked = ObservableBoolean(false)
    val isFavorite = ObservableBoolean(false)

    val sameGroups: ObservableList<Group> = ObservableArrayList()

    private val disposables = CompositeDisposable()

    fun onStart() {
//        when (type) {
//            is UserDetailDialogType.Single -> onSingleUserDetailDialogType(type.userId)
//            UserDetailDialogType.WithSwitches -> onWithSwitchesUserDetailDialogType()
//        }
    }

    private fun onSingleUserDetailDialogType(userId: Int) {
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
                            showError(resourceManager.getString(R.string.unknown_error)) { onSingleUserDetailDialogType(userId) }
                        }
                        is Optional.Value -> {
                            val (user, sameGroups, userTypes) = optionalInfo.value
                            setUserInfo(user, sameGroups, userTypes)
                        }
                    }
                },
                {
                    showError(resourceManager.getString(R.string.unknown_error)) { onSingleUserDetailDialogType(userId) }
                }
            ).addTo(disposables)
    }

    private fun onWithSwitchesUserDetailDialogType() {
        localUsersRepo.getUserWithMaxGroupsCountWithFilters()
            .flatMap { userWithMaxGroupsCountResult ->
                when (userWithMaxGroupsCountResult) {
                    UserWithMaxGroupsCountResult.Empty -> {
                        Single.just(Optional.empty())
                    }
                    is UserWithMaxGroupsCountResult.Result -> {
                        val user = userWithMaxGroupsCountResult.user
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

                        }
                        is Optional.Value -> {
                            val (user, sameGroups, userTypes) = optionalInfo.value
                            setUserInfo(user, sameGroups, userTypes)
                        }
                    }
                },
                {
                    showError(resourceManager.getString(R.string.unknown_error)) { onWithSwitchesUserDetailDialogType() }
                }
            ).addTo(disposables)
    }

    private fun getSameGroupsAndTypes(userId: Int): Single<Pair<List<Group>, List<UserType>>> {
        return localGroupsRepo.getSameGroupsWithUser(userId)
            .flatMap { sameGroups ->
                localUsersRepo.getTypes(userId).map { userTypes ->
                    Pair(sameGroups, userTypes)
                }
            }
    }

    private fun setUserInfo(user: User, sameGroups: List<Group>, userTypes: List<UserType>) {
        userName.set("${user.firstName} ${user.lastName}")
        userAge.set(user.age.toString())
        user.sex?.let { sex -> resourceManager.getString(sex.getStringResource()) }
        user.relation?.let { relation -> resourceManager.getString(relation.getStringResource()) }
        userCountry.set(user.country?.title)
        userCity.set(user.city?.title)
        isViewed.set(userTypes.any { it.id == UserType.VIEWED.id })
        isBlocked.set(userTypes.any { it.id == UserType.BLOCKED.id })
        isFavorite.set(userTypes.any { it.id == UserType.FAVORITE.id })
        this.sameGroups.clear()
        this.sameGroups.addAll(sameGroups)
        infoVisible.set(true)
    }

    private fun showError(errorText: String, retryFunction: (() -> Unit)? = null) {
        this.errorText.set(errorText)
        this.retryFunction.set(retryFunction)
        infoVisible.set(false)
        loadingVisible.set(false)
        errorVisible.set(true)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(type: UserDetailDialogType): UserDetailViewModel
    }

}