package ru.g0rd1.peoplesfinder.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.g0rd1.peoplesfinder.apiservice.ApiClient
import ru.g0rd1.peoplesfinder.apiservice.ApiClientFactory
import ru.g0rd1.peoplesfinder.base.navigator.AppNavigator
import ru.g0rd1.peoplesfinder.base.navigator.Navigator
import ru.g0rd1.peoplesfinder.base.scheduler.AppSchedulers
import ru.g0rd1.peoplesfinder.base.scheduler.Schedulers
import javax.inject.Singleton

@Module
abstract class GlobalModule {

    @Binds
    @Singleton
    abstract fun schedulers(schedulers: AppSchedulers): Schedulers

    @Binds
    @Singleton
    abstract fun navigator(navigator: AppNavigator): Navigator

    @Module
    companion object {

        @JvmStatic
        @Provides
        @Singleton
        fun apiClient(apiClientFactory: ApiClientFactory): ApiClient = apiClientFactory.create()

    }

}
