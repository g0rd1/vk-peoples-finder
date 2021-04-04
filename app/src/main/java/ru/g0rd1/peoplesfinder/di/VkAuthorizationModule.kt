package ru.g0rd1.peoplesfinder.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.g0rd1.peoplesfinder.base.auhorization.VkAuthorizationContract
import ru.g0rd1.peoplesfinder.base.auhorization.VkAuthorizationManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class VkAuthorizationModule {

    @Binds
    @Singleton
    abstract fun vkAuthorizationManager(vkAuthorizationManager: VkAuthorizationManager): VkAuthorizationContract.Manager

    companion object {

        @Provides
        fun vkAuthorizationHelper(
            vkAuthorizationManager: VkAuthorizationContract.Manager
        ): VkAuthorizationContract.Helper = vkAuthorizationManager.getHelper()

    }

}