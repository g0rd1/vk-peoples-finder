package ru.g0rd1.peoplesfinder.di.fragment

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.g0rd1.peoplesfinder.ui.authorization.AuthorizationFragment
import ru.g0rd1.peoplesfinder.ui.groups.GroupsFragment

@Module
abstract class FragmentsModule {

    @ContributesAndroidInjector(modules = [AuthorizationFragmentModule::class])
    abstract fun loginFragment(): AuthorizationFragment

    @ContributesAndroidInjector(modules = [GroupsFragmentModule::class])
    abstract fun groupsFragment(): GroupsFragment

}