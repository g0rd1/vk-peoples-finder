package ru.g0rd1.peoplesfinder.ui.userDetail

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import io.reactivex.Completable
import io.reactivex.Single
import ru.g0rd1.peoplesfinder.base.BaseViewModel
import ru.g0rd1.peoplesfinder.base.global.SingleLiveEvent
import ru.g0rd1.peoplesfinder.base.navigator.AppNavigator
import ru.g0rd1.peoplesfinder.common.ResourceManager
import ru.g0rd1.peoplesfinder.common.enums.Sex
import ru.g0rd1.peoplesfinder.model.Group
import ru.g0rd1.peoplesfinder.model.User
import ru.g0rd1.peoplesfinder.model.UserType
import ru.g0rd1.peoplesfinder.repo.group.local.LocalGroupsRepo
import ru.g0rd1.peoplesfinder.repo.user.local.LocalUsersRepo
import ru.g0rd1.peoplesfinder.util.observeOnUI

abstract class UserDetailViewModel constructor(
    private val localUsersRepo: LocalUsersRepo,
    private val localGroupsRepo: LocalGroupsRepo,
    private val resourceManager: ResourceManager,
    private val appNavigator: AppNavigator,
) : BaseViewModel() {

    val showLeftArrow = ObservableBoolean(false)
    val showRightArrow = ObservableBoolean(false)

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

    val isBlocked = ObservableBoolean(false)
    val isFavorite = ObservableBoolean(false)

    val showPhotosEvent = SingleLiveEvent<Int>()

    private var userId: Int = -1

    val sameGroups = ObservableField<List<Group>>()

    fun showPhotos() {
        showPhotosEvent.value = userId
    }

    abstract fun previousUser()

    abstract fun nextUser()

    fun clickOnBlocked() {
        switchUserType(UserType.BLOCKED)
    }

    fun clickOnFavorite() {
        switchUserType(UserType.FAVORITE)
    }

    private fun switchUserType(userType: UserType) {
        localUsersRepo.switchTypeStatus(userId, userType)
            .observeOnUI()
            .andThen(onUserChanged())
            .execute()
    }

    abstract fun onUserChanged(): Completable

    protected fun getSameGroupsAndTypes(userId: Int): Single<Pair<List<Group>, List<UserType>>> {
        return localGroupsRepo.getSameGroupsWithUser(userId)
            .flatMap { sameGroups ->
                localUsersRepo.getTypes(userId).map { userTypes ->
                    Pair(sameGroups, userTypes)
                }
            }
    }

    protected fun setUserInfo(user: User, sameGroups: List<Group>, userTypes: List<UserType>) {
        this.userId = user.id
        userName.set("${user.firstName} ${user.lastName}")
        userAge.set(user.age?.toString())
        user.sex?.let { sex -> resourceManager.getString(sex.getStringResource()) }
        user.relation?.let { relation ->
            resourceManager.getString(relation.getStringResource(user.sex ?: Sex.NOT_SPECIFIED))
        }
        userCountry.set(user.country?.title)
        userCity.set(user.city?.title)
        isBlocked.set(userTypes.any { it.id == UserType.BLOCKED.id })
        isFavorite.set(userTypes.any { it.id == UserType.FAVORITE.id })
        this.sameGroups.set(sameGroups)
        imageUrl.set(user.photoMax/*.also { Timber.d("TEST: ${user.id} $it") }*/)
        infoVisible.set(true)
    }

    protected fun showError(errorText: String, retryFunction: (() -> Unit)? = null) {
        this.errorText.set(errorText)
        this.retryFunction.set(retryFunction)
        infoVisible.set(false)
        loadingVisible.set(false)
        errorVisible.set(true)
    }

    interface Factory {
        fun create(
            dialogType: UserDetailDialogType,
            fragment: UserDetailDialog,
        ): UserDetailViewModel
    }

}