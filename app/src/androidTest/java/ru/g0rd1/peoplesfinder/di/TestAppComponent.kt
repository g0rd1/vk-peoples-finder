package ru.g0rd1.peoplesfinder.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import ru.g0rd1.peoplesfinder.base.TestBaseApplication
import ru.g0rd1.peoplesfinder.control.groupmembersloader.GroupMembersLoaderTest
import ru.g0rd1.peoplesfinder.repo.LocalUserRepoAndLocalGroupRepoTest
import ru.g0rd1.peoplesfinder.repo.group.local.LocalGroupsRepoTest
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        // ActivitiesModule::class,
        GlobalModule::class,
        TestReposModule::class,
        TestDatabaseModule::class,
        // VkAuthorizationModule::class,
        TestGroupMembersLoaderModule::class
    ]
)
interface TestAppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): TestAppComponent
    }

    fun inject(app: TestBaseApplication)

    fun inject(localGroupsRepoTest: LocalGroupsRepoTest)

    fun inject(GroupMembersLoaderTest: GroupMembersLoaderTest)

    fun inject(localUserRepoAndLocalGroupRepoTest: LocalUserRepoAndLocalGroupRepoTest)

}