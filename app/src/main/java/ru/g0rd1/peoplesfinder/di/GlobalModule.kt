package ru.g0rd1.peoplesfinder.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.g0rd1.peoplesfinder.apiservice.ApiClient
import ru.g0rd1.peoplesfinder.apiservice.ApiClientFactory
import ru.g0rd1.peoplesfinder.apiservice.PriorityQueueApiClient
import ru.g0rd1.peoplesfinder.base.navigator.AppNavigator
import ru.g0rd1.peoplesfinder.base.navigator.Navigator
import ru.g0rd1.peoplesfinder.common.AppResourceManager
import ru.g0rd1.peoplesfinder.common.PriorityQueueManagerFactory
import ru.g0rd1.peoplesfinder.common.ResourceManager
import javax.inject.Singleton

@Module
abstract class GlobalModule {

    @Binds
    @Singleton
    abstract fun navigator(navigator: AppNavigator): Navigator

    @Binds
    @Singleton
    abstract fun resManager(resManager: AppResourceManager): ResourceManager

    companion object {

        @Provides
        @Singleton
        fun apiClient(
            apiClientFactory: ApiClientFactory,
            priorityQueueManagerFactory: PriorityQueueManagerFactory
        ): ApiClient =
            PriorityQueueApiClient(
                apiClientFactory.create(),
                priorityQueueManagerFactory.create(API_CLIENT_QUEUE_CAPACITY)
            )

        private const val API_CLIENT_QUEUE_CAPACITY = 3
    }

}
