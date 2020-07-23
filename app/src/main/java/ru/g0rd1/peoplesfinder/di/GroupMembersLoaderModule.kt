package ru.g0rd1.peoplesfinder.di

import dagger.Binds
import dagger.Module
import ru.g0rd1.peoplesfinder.control.groupmembersloader.GroupMembersLoader
import ru.g0rd1.peoplesfinder.control.groupmembersloader.GroupMembersLoaderManager
import ru.g0rd1.peoplesfinder.control.groupmembersloader.GroupMembersLoaderRegulator
import ru.g0rd1.peoplesfinder.control.groupmembersloader.HttpGroupMembersLoaderFactory
import javax.inject.Singleton

@Module
abstract class GroupMembersLoaderModule {

    @Binds
    @Singleton
    abstract fun factory(factory: HttpGroupMembersLoaderFactory): GroupMembersLoader.Factory

    @Binds
    @Singleton
    abstract fun manager(manager: GroupMembersLoaderManager): GroupMembersLoader.Manager

    @Binds
    @Singleton
    abstract fun regulator(regulator: GroupMembersLoaderRegulator): GroupMembersLoader.Regulator

}