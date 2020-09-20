package ru.g0rd1.peoplesfinder.ui.results

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import ru.g0rd1.peoplesfinder.base.error.Error
import ru.g0rd1.peoplesfinder.base.scheduler.Schedulers
import ru.g0rd1.peoplesfinder.repo.user.local.LocalUsersRepo
import timber.log.Timber
import javax.inject.Inject

class ResultsPresenter @Inject constructor(
    private val localUserRepo: LocalUsersRepo,
    private val schedulers: Schedulers,
    private val errorHandler: Error.Handler
) : ResultsContract.Presenter {

    private lateinit var view: ResultsContract.View

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun setView(view: ResultsContract.View) {
        this.view = view
    }

    override fun onStart() {
        Timber.d("onStart()")
        localUserRepo.getWithSameGroupsCount()
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.main())
            .subscribe(
                { users ->
                    Timber.d("users size: ${users.size}")
                    Timber.d("${users.take(10).map { Pair(it.id, it.sameGroupsCount) }}")
                    Timber.d("${users.take(10)}")
//                    Timber.d("users: $users")
                    view.setUsers(users.take(10))
                },
                {
                    errorHandler.handle(it)
                }
            )
            .addTo(compositeDisposable)
    }

    override fun onStop() {
        compositeDisposable.dispose()
    }

}