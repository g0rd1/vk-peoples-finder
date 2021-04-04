package ru.g0rd1.peoplesfinder.base

import android.app.Application
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKTokenExpiredHandler
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

    private val tokenTracker = object : VKTokenExpiredHandler {
        override fun onTokenExpired() {
            appNavigator.authorization()
        }
    }

    override fun onCreate() {
        super.onCreate()
        VK.addTokenExpiredHandler(tokenTracker)
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }
    }

}