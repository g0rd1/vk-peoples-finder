package ru.g0rd1.peoplesfinder.di

import dagger.Module
import dagger.Provides
import ru.g0rd1.peoplesfinder.base.scheduler.Schedulers
import ru.g0rd1.peoplesfinder.control.groupmembersloader.GroupMembersLoader
import ru.g0rd1.peoplesfinder.control.groupmembersloader.GroupMembersLoaderManager
import ru.g0rd1.peoplesfinder.control.groupmembersloader.GroupMembersLoaderRegulator
import ru.g0rd1.peoplesfinder.control.groupmembersloader.HttpGroupMembersLoaderFactory
import ru.g0rd1.peoplesfinder.repo.group.local.LocalGroupsRepo
import ru.g0rd1.peoplesfinder.repo.group.vk.VkGroupsRepo
import ru.g0rd1.peoplesfinder.repo.user.local.LocalUsersRepo
import javax.inject.Named
import javax.inject.Singleton

@Module
abstract class TestGroupMembersLoaderModule : GroupMembersLoaderModule() {


    @Module
    companion object {

        @JvmStatic
        @Provides
        @Singleton
        @Named("TEST")
        fun regulator(): GroupMembersLoader.Regulator {
            return GroupMembersLoaderRegulator()
        }

        @JvmStatic
        @Provides
        @Singleton
        @Named("TEST")
        fun factory(
            @Named("TEST")
            vkGroupsRepo: VkGroupsRepo,
            schedulers: Schedulers,
            @Named("TEST")
            localUsersRepo: LocalUsersRepo,
            @Named("TEST")
            localGroupsRepo: LocalGroupsRepo,
            @Named("TEST")
            regulator: GroupMembersLoader.Regulator
        ): GroupMembersLoader.Factory = HttpGroupMembersLoaderFactory(
            vkGroupsRepo,
            schedulers,
            localUsersRepo,
            localGroupsRepo,
            regulator
        )

        @JvmStatic
        @Provides
        @Singleton
        @Named("TEST")
        fun manager(
            @Named("TEST") groupMembersLoaderFactory: GroupMembersLoader.Factory
        ): GroupMembersLoader.Manager = GroupMembersLoaderManager(groupMembersLoaderFactory)
    }


}