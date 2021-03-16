package ru.g0rd1.peoplesfinder.di.activity

import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Module

@AssistedModule
@Module(includes = [AssistedInject_AssistedInjectionModule::class])
abstract class AssistedInjectionModule {}