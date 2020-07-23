package ru.g0rd1.peoplesfinder.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.g0rd1.peoplesfinder.repo.access.SharedPrefVKAccessRepo
import ru.g0rd1.peoplesfinder.repo.access.VKAccessRepo
import ru.g0rd1.peoplesfinder.repo.group.local.DBLocalGroupsRepo
import ru.g0rd1.peoplesfinder.repo.group.local.LocalGroupsRepo
import ru.g0rd1.peoplesfinder.repo.group.local.QueueLocalGroupsRepo
import ru.g0rd1.peoplesfinder.repo.group.vk.HttpVkGroupsRepo
import ru.g0rd1.peoplesfinder.repo.group.vk.VkGroupsRepo
import ru.g0rd1.peoplesfinder.repo.user.local.DBLocalUsersRepo
import ru.g0rd1.peoplesfinder.repo.user.local.LocalUsersRepo
import ru.g0rd1.peoplesfinder.repo.user.local.QueueLocalUsersRepo
import javax.inject.Named
import javax.inject.Singleton

@Module
abstract class ReposModule {

    @Binds
    @Singleton
    abstract fun groupsVkRepo(groupsVkRepo: HttpVkGroupsRepo): VkGroupsRepo

    @Binds
    @Singleton
    abstract fun groupsRepo(groupsRepo: DBLocalGroupsRepo): LocalGroupsRepo

    @Binds
    @Singleton
    abstract fun usersRepo(usersRepo: DBLocalUsersRepo): LocalUsersRepo

    @Binds
    @Singleton
    abstract fun tokenRepo(tokenRepo: SharedPrefVKAccessRepo): VKAccessRepo

    companion object {

        @JvmStatic
        @Singleton
        @Provides
        @Named(LocalUsersRepo.QUEUE)
        fun queueLocalUserRepo(localUsersRepo: LocalUsersRepo): LocalUsersRepo {
            return QueueLocalUsersRepo(localUsersRepo)
        }

        @JvmStatic
        @Singleton
        @Provides
        @Named(LocalGroupsRepo.QUEUE)
        fun queueLocalGroupRepo(localGroupsRepo: LocalGroupsRepo): LocalGroupsRepo {
            return QueueLocalGroupsRepo(localGroupsRepo)
        }

    }

}