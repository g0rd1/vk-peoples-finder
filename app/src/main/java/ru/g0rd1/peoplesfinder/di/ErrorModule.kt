package ru.g0rd1.peoplesfinder.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import ru.g0rd1.peoplesfinder.base.error.Error
import ru.g0rd1.peoplesfinder.base.error.ErrorHandler
import ru.g0rd1.peoplesfinder.base.error.ErrorPresenter

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class ErrorModule {

    @Binds
    abstract fun errorPresenter(errorPresenter: ErrorPresenter): Error.Presenter

    @Binds
    abstract fun errorHandler(errorHandler: ErrorHandler): Error.Handler

}