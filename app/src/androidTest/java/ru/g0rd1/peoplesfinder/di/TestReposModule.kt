package ru.g0rd1.peoplesfinder.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.g0rd1.peoplesfinder.db.dao.GroupDao
import ru.g0rd1.peoplesfinder.db.dao.UserDao
import ru.g0rd1.peoplesfinder.repo.access.SharedPrefVKAccessRepo
import ru.g0rd1.peoplesfinder.repo.access.VKAccessRepo
import ru.g0rd1.peoplesfinder.repo.group.local.DBLocalGroupsRepo
import ru.g0rd1.peoplesfinder.repo.group.local.LocalGroupsRepo
import ru.g0rd1.peoplesfinder.repo.group.vk.TestVkGroupsRepo
import ru.g0rd1.peoplesfinder.repo.group.vk.VkGroupsRepo
import ru.g0rd1.peoplesfinder.repo.user.local.DBLocalUsersRepo
import ru.g0rd1.peoplesfinder.repo.user.local.LocalUsersRepo
import javax.inject.Named
import javax.inject.Singleton

@Module
abstract class TestReposModule : ReposModule() {

    @Module
    companion object {

        @JvmStatic
        @Provides
        @Singleton
        @Named("TEST")
        fun testGroupsVkRepo(): VkGroupsRepo = TestVkGroupsRepo()

        @JvmStatic
        @Provides
        @Singleton
        @Named("TEST")
        fun groupsRepo(@Named("TEST") groupDao: GroupDao): LocalGroupsRepo =
            DBLocalGroupsRepo(groupDao)

        @JvmStatic
        @Provides
        @Singleton
        @Named("TEST")
        fun usersRepo(@Named("TEST") userDao: UserDao): LocalUsersRepo = DBLocalUsersRepo(userDao)

        @JvmStatic
        @Provides
        @Singleton
        @Named("TEST")
        fun tokenRepo(context: Context): VKAccessRepo = SharedPrefVKAccessRepo(context)

    }

}