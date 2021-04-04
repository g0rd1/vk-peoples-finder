package ru.g0rd1.peoplesfinder.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.g0rd1.peoplesfinder.control.groupmembersloader.GroupMembersLoader
import ru.g0rd1.peoplesfinder.control.groupmembersloader.GroupMembersLoaderManager
import ru.g0rd1.peoplesfinder.control.groupmembersloader.HttpGroupMembersLoaderFactory
import ru.g0rd1.peoplesfinder.control.groupmembersloader.HttpGroupMembersLoaderManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class GroupMembersLoaderModule {

    @Binds
    @Singleton
    abstract fun factory(factory: HttpGroupMembersLoaderFactory): GroupMembersLoader.Factory

    @Binds
    @Singleton
    abstract fun manager(manager: HttpGroupMembersLoaderManager): GroupMembersLoaderManager

}