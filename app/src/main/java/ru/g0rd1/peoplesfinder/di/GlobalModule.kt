package ru.g0rd1.peoplesfinder.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.g0rd1.peoplesfinder.apiservice.ApiClient
import ru.g0rd1.peoplesfinder.apiservice.ApiClientFactory
import ru.g0rd1.peoplesfinder.apiservice.PriorityQueueApiClient
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

    companion object {

        @Provides
        @Singleton
        fun apiClient(apiClientFactory: ApiClientFactory): ApiClient =
            PriorityQueueApiClient(apiClientFactory.create())

    }

}
