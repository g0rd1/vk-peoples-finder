package ru.g0rd1.peoplesfinder.base

import android.app.Application
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKTokenExpiredHandler
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import ru.g0rd1.peoplesfinder.BuildConfig
import ru.g0rd1.peoplesfinder.base.navigator.Navigator
import ru.g0rd1.peoplesfinder.base.timber.ReleaseTree
import ru.g0rd1.peoplesfinder.di.DaggerTestAppComponent
import ru.g0rd1.peoplesfinder.di.TestAppComponent
import timber.log.Timber
import timber.log.Timber.DebugTree
import javax.inject.Inject


open class TestBaseApplication : Application(), HasAndroidInjector {

    @Inject
    open lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @Inject
    open lateinit var navigator: Navigator

    lateinit var appComponent: TestAppComponent

    private val tokenTracker = object : VKTokenExpiredHandler {
        override fun onTokenExpired() {
            navigator.authorization()
        }
    }

    override fun onCreate() {
        super.onCreate()
        VK.addTokenExpiredHandler(tokenTracker)
        initAppComponent()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }
    }

    protected open fun initAppComponent() {
        appComponent = DaggerTestAppComponent.factory().create(this)
        appComponent.inject(this)
    }

    override fun androidInjector(): DispatchingAndroidInjector<Any> {
        return androidInjector
    }

}