package ru.g0rd1.peoplesfinder.ui.lists

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.g0rd1.peoplesfinder.base.BaseViewModel
import ru.g0rd1.peoplesfinder.model.UserType
import ru.g0rd1.peoplesfinder.repo.user.local.LocalUsersRepo
import ru.g0rd1.peoplesfinder.util.observeOnUI
import javax.inject.Inject

@HiltViewModel
class ListsViewModel @Inject constructor(
    private val userRepo: LocalUsersRepo,
) : BaseViewModel() {

    private var userTypeDisposable: Disposable? = null

    private val _state: MutableStateFlow<ListsViewData> = MutableStateFlow(ListsViewData.Loading)

    val state: StateFlow<ListsViewData> = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = ListsViewData.Loading
    )

    override fun onStart() {
        onFavoriteClick()
    }

    fun onFavoriteClick() {
        getUsersByType(UserType.FAVORITE)
    }

    fun onBlockedClick() {
        getUsersByType(UserType.BLOCKED)
    }

    private fun getUsersByType(userType: UserType) {
        userTypeDisposable?.dispose()
        userRepo.getUserByType(userType.id)
            .observeOnUI()
            .subscribe(
                { users ->
                    viewModelScope.launch {
                        _state.emit(
                            ListsViewData.Data(
                                userType = userType,
                                users = users
                            )
                        )
                    }
                },
                {
                    // TODO обработть ошибку
                }
            )
            .also { userTypeDisposable = it }
    }

}