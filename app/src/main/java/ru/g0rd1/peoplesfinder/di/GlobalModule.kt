package ru.g0rd1.peoplesfinder.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.g0rd1.peoplesfinder.apiservice.ApiClient
import ru.g0rd1.peoplesfinder.apiservice.ApiClientFactory
import ru.g0rd1.peoplesfinder.apiservice.PriorityQueueApiClient
import ru.g0rd1.peoplesfinder.base.navigator.AppNavigator
import ru.g0rd1.peoplesfinder.base.navigator.SimpleAppNavigator
import ru.g0rd1.peoplesfinder.common.AppResourceManager
import ru.g0rd1.peoplesfinder.common.PriorityQueueManagerFactory
import ru.g0rd1.peoplesfinder.common.ResourceManager
import ru.g0rd1.peoplesfinder.repo.access.VKAccessRepo
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class GlobalModule {

    @Binds
    @Singleton
    abstract fun navigator(navigator: SimpleAppNavigator): AppNavigator

    // @Binds
    // @Singleton
    // abstract fun resManager(resManager: AppResourceManager): ResourceManager

    companion object {

        @Provides
        @Singleton
        fun resManager(@ApplicationContext context: Context): ResourceManager = AppResourceManager(context)

        @Provides
        @Singleton
        fun apiClient(
            apiClientFactory: ApiClientFactory,
            vkAccessRepo: VKAccessRepo,
            priorityQueueManagerFactory: PriorityQueueManagerFactory
        ): ApiClient =
            PriorityQueueApiClient(
                apiClientFactory.create(),
                vkAccessRepo,
                priorityQueueManagerFactory.create(API_CLIENT_QUEUE_CAPACITY)
            )

        private const val API_CLIENT_QUEUE_CAPACITY = 3
    }

}
