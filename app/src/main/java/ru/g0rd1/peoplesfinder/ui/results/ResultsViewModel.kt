package ru.g0rd1.peoplesfinder.ui.results

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import ru.g0rd1.peoplesfinder.base.error.Error
import ru.g0rd1.peoplesfinder.repo.user.local.LocalUsersRepo
import ru.g0rd1.peoplesfinder.util.observeOnUI
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ResultsViewModel @Inject constructor(
    private val localUserRepo: LocalUsersRepo,
    private val errorHandler: Error.Handler
) : ViewModel() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    val userViewModels: ObservableList<UserViewModel> = ObservableArrayList()

    init {
        localUserRepo.getWithSameGroupsCount()
            .throttleLatest(USER_UPDATE_INTERVAL_SECONDS, TimeUnit.SECONDS)
            .observeOnUI()
            .subscribe(
                {
                    userViewModels.clear()
                    userViewModels.addAll(
                        it.map { (user, sameGroupCount) ->
                            UserViewModel(user, sameGroupCount)
                        }
                    )
                },
                {
                    errorHandler.handle(it)
                }
            )
            .addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    companion object {
        private const val USER_UPDATE_INTERVAL_SECONDS = 1L
    }

}