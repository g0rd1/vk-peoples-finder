package ru.g0rd1.peoplesfinder.di

import dagger.Module
import dagger.Provides
import ru.g0rd1.peoplesfinder.base.scheduler.Schedulers
import ru.g0rd1.peoplesfinder.control.groupmembersloader.GroupMembersLoader
import ru.g0rd1.peoplesfinder.control.groupmembersloader.GroupMembersLoaderManager
import ru.g0rd1.peoplesfinder.control.groupmembersloader.GroupMembersLoaderRegulator
import ru.g0rd1.peoplesfinder.control.groupmembersloader.HttpGroupMembersLoaderFactory
import ru.g0rd1.peoplesfinder.repo.group.local.LocalGroupsRepo
import ru.g0rd1.peoplesfinder.repo.group.vk.VkGroupsMembersRepo
import ru.g0rd1.peoplesfinder.repo.user.local.LocalUsersRepo
import javax.inject.Named
import javax.inject.Singleton

@Module
abstract class TestGroupMembersLoaderModule : GroupMembersLoaderModule() {

    companion object {

        @Provides
        @Singleton
        @Named("TEST")
        fun regulator(): GroupMembersLoader.Regulator {
            return GroupMembersLoaderRegulator()
        }

        @Provides
        @Singleton
        @Named("TEST")
        fun factory(
            @Named("TEST")
            vkGroupsMembersRepo: VkGroupsMembersRepo,
            schedulers: Schedulers,
            @Named("TEST")
            localUsersRepo: LocalUsersRepo,
            @Named("TEST")
            localGroupsRepo: LocalGroupsRepo
        ): GroupMembersLoader.Factory = HttpGroupMembersLoaderFactory(
            vkGroupsMembersRepo,
            schedulers,
            localUsersRepo,
            localGroupsRepo
        )

        @Provides
        @Singleton
        @Named("TEST")
        fun manager(
            @Named("TEST") groupMembersLoaderFactory: GroupMembersLoader.Factory
        ): GroupMembersLoader.Manager = GroupMembersLoaderManager(groupMembersLoaderFactory)
    }


}