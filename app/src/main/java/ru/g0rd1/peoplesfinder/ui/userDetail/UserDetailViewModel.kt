package ru.g0rd1.peoplesfinder.ui.userDetail

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.Single
import ru.g0rd1.peoplesfinder.R
import ru.g0rd1.peoplesfinder.base.BaseViewModel
import ru.g0rd1.peoplesfinder.base.global.SingleLiveEvent
import ru.g0rd1.peoplesfinder.base.navigator.AppNavigator
import ru.g0rd1.peoplesfinder.common.ResourceManager
import ru.g0rd1.peoplesfinder.common.enums.Sex
import ru.g0rd1.peoplesfinder.model.Group
import ru.g0rd1.peoplesfinder.model.Optional
import ru.g0rd1.peoplesfinder.model.User
import ru.g0rd1.peoplesfinder.model.UserType
import ru.g0rd1.peoplesfinder.model.result.UserWithSameGroupsCountResult
import ru.g0rd1.peoplesfinder.repo.group.local.LocalGroupsRepo
import ru.g0rd1.peoplesfinder.repo.user.local.LocalUsersRepo
import ru.g0rd1.peoplesfinder.util.observeOnUI
import timber.log.Timber

class UserDetailViewModel @AssistedInject constructor(
    @Assisted private val type: UserDetailDialogType,
    private val localUsersRepo: LocalUsersRepo,
    private val localGroupsRepo: LocalGroupsRepo,
    private val resourceManager: ResourceManager,
    private val appNavigator: AppNavigator,
) : BaseViewModel() {

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
    val imageUrl = ObservableField<String>()

    val isViewed = ObservableBoolean(false)
    val isBlocked = ObservableBoolean(false)
    val isFavorite = ObservableBoolean(false)

    val showPhotosEvent = SingleLiveEvent<Int>()

    private var userId: Int = -1

    val sameGroups = ObservableField<List<Group>>()

    override fun onStart() {
        when (type) {
            is UserDetailDialogType.Single -> onSingleUserDetailDialogType(type.userId)
            UserDetailDialogType.WithSwitches -> onWithSwitchesUserDetailDialogType()
        }
    }

    fun showPhotos() {
        showPhotosEvent.value = userId
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
                            showError(resourceManager.getString(R.string.unknown_error)) {
                                onSingleUserDetailDialogType(userId)
                            }
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
            ).disposeLater()
    }

    private fun onWithSwitchesUserDetailDialogType() {
        localUsersRepo.getUserWithMaxSameGroupsCountWithFilters(listOf(UserType.BLOCKED, UserType.VIEWED))
            .flatMap { userWithMaxGroupsCountResult ->
                when (userWithMaxGroupsCountResult) {
                    UserWithSameGroupsCountResult.Empty -> {
                        // TODO Сделать запрос групп, чтобы понять если просмотренный группы соответсвующие фильтрам
                        Single.just(Optional.empty())
                    }
                    is UserWithSameGroupsCountResult.Result -> {
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
                            // TODO Выдавать сообщение о том что результатов нет
                        }
                        is Optional.Value -> {
                            val (user, sameGroups, userTypes) = optionalInfo.value
                            setUserInfo(user, sameGroups, userTypes)
                            infoVisible.set(true)
                            loadingVisible.set(false)
                            errorVisible.set(false)
                        }
                    }
                },
                {
                    showError(resourceManager.getString(R.string.unknown_error)) { onWithSwitchesUserDetailDialogType() }
                }
            ).disposeLater()
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
        this.userId = user.id
        userName.set("${user.firstName} ${user.lastName}")
        userAge.set(user.age?.toString())
        user.sex?.let { sex -> resourceManager.getString(sex.getStringResource()) }
        user.relation?.let { relation ->
            resourceManager.getString(relation.getStringResource(user.sex ?: Sex.NOT_SPECIFIED))
        }
        userCountry.set(user.country?.title)
        userCity.set(user.city?.title)
        isViewed.set(userTypes.any { it.id == UserType.VIEWED.id })
        isBlocked.set(userTypes.any { it.id == UserType.BLOCKED.id })
        isFavorite.set(userTypes.any { it.id == UserType.FAVORITE.id })
        this.sameGroups.set(sameGroups)
        imageUrl.set(user.photoMax.also { Timber.d("TEST: ${user.id} $it") })
        infoVisible.set(true)
    }

    private fun showError(errorText: String, retryFunction: (() -> Unit)? = null) {
        this.errorText.set(errorText)
        this.retryFunction.set(retryFunction)
        infoVisible.set(false)
        loadingVisible.set(false)
        errorVisible.set(true)
    }

    @AssistedFactory
    interface Factory {
        fun create(type: UserDetailDialogType): UserDetailViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            type: UserDetailDialogType
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return assistedFactory.create(type) as T
            }
        }
    }

}