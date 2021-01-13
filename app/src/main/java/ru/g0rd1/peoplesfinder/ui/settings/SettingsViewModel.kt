package ru.g0rd1.peoplesfinder.ui.settings

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

class SettingsViewModel @Inject constructor() : ViewModel() {

    private val disposables = CompositeDisposable()

    fun onStart() {
        Timber.d("TEST SettingsViewModel init")
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

}