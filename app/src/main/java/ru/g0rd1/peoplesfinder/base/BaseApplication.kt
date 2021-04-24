package ru.g0rd1.peoplesfinder.base

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import ru.g0rd1.peoplesfinder.BuildConfig
import ru.g0rd1.peoplesfinder.base.navigator.AppNavigator
import ru.g0rd1.peoplesfinder.base.timber.ReleaseTree
import timber.log.Timber
import timber.log.Timber.DebugTree
import javax.inject.Inject

@HiltAndroidApp
open class BaseApplication : Application() {

    @Inject
    open lateinit var appNavigator: AppNavigator

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }
    }

}