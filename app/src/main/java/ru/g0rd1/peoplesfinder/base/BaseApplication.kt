package ru.g0rd1.peoplesfinder.base

import android.app.Application
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKTokenExpiredHandler
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import ru.g0rd1.peoplesfinder.BuildConfig
import ru.g0rd1.peoplesfinder.base.navigator.Navigator
import ru.g0rd1.peoplesfinder.base.timber.ReleaseTree
import ru.g0rd1.peoplesfinder.di.DaggerAppComponent
import timber.log.Timber
import timber.log.Timber.DebugTree
import javax.inject.Inject


open class BaseApplication : Application(), HasAndroidInjector {

    @Inject
    open lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @Inject
    open lateinit var navigator: Navigator

    private val tokenTracker = object : VKTokenExpiredHandler {
        override fun onTokenExpired() {
            navigator.authorize()
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        VK.addTokenExpiredHandler(tokenTracker)
        initAppComponent()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }
    }

    protected open fun initAppComponent() {
        DaggerAppComponent.factory().create(this).also { it.inject(this) }
    }

    override fun androidInjector(): DispatchingAndroidInjector<Any> {
        return androidInjector
    }

    companion object {
        lateinit var instance: BaseApplication
            private set
    }

}