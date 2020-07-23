package ru.g0rd1.peoplesfinder.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import ru.g0rd1.peoplesfinder.base.BaseApplication
import ru.g0rd1.peoplesfinder.di.activity.ActivitiesModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        ActivitiesModule::class,
        GlobalModule::class,
        ReposModule::class,
        DatabaseModule::class,
        VkAuthorizationModule::class,
        GroupMembersLoaderModule::class
    ]
)
interface AppComponent {

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(app: BaseApplication)
}