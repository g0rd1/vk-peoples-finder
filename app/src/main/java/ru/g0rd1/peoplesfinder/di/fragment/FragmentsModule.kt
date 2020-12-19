package ru.g0rd1.peoplesfinder.di.fragment

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.g0rd1.peoplesfinder.ui.authorization.AuthorizationFragment
import ru.g0rd1.peoplesfinder.ui.groups.GroupsFragment
import ru.g0rd1.peoplesfinder.ui.results.ResultsFragment
import ru.g0rd1.peoplesfinder.ui.settings.SettingsFragment
import ru.g0rd1.peoplesfinder.ui.synchronization.SynchronizationFragment

@Module
abstract class FragmentsModule {

    @ContributesAndroidInjector(modules = [ResultsFragmentModule::class])
    abstract fun resultsFragment(): ResultsFragment

    @ContributesAndroidInjector(modules = [SettingsFragmentModule::class])
    abstract fun settingsFragment(): SettingsFragment

    @ContributesAndroidInjector(modules = [GroupsFragmentModule::class])
    abstract fun groupsFragment(): GroupsFragment

    @ContributesAndroidInjector(modules = [AuthorizationFragmentModule::class])
    abstract fun authorizationFragment(): AuthorizationFragment

    @ContributesAndroidInjector(modules = [SynchronizationFragmentModule::class])
    abstract fun synchronizationFragment(): SynchronizationFragment

}