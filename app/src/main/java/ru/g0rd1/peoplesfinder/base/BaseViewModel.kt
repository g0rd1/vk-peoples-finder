package ru.g0rd1.peoplesfinder.base

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import timber.log.Timber

abstract class BaseViewModel : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    @CallSuper
    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    protected fun Disposable.disposeLater() {
        compositeDisposable.add(this)
    }

    protected fun Completable.execute() {
        this.subscribe({}, Timber::e).disposeLater()
    }

    abstract fun onStart()

}
