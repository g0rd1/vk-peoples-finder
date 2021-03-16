package ru.g0rd1.peoplesfinder.di.activity

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.g0rd1.peoplesfinder.base.main.MainActivity
import ru.g0rd1.peoplesfinder.di.ErrorModule
import ru.g0rd1.peoplesfinder.di.fragment.FragmentsModule

@Module
abstract class ActivitiesModule {

    @PerMainActivity
    @ContributesAndroidInjector(
        modules = [
            MainActivityModule::class,
            FragmentsModule::class,
            ErrorModule::class,
            AssistedInjectionModule::class
        ]
    )
    abstract fun contributesMainActivity(): MainActivity
}