package ru.g0rd1.peoplesfinder.di

import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.g0rd1.peoplesfinder.base.error.Error
import ru.g0rd1.peoplesfinder.base.error.ErrorDialogFragment
import ru.g0rd1.peoplesfinder.base.error.ErrorHandler
import ru.g0rd1.peoplesfinder.base.error.ErrorPresenter

@Module
abstract class ErrorModule {

    @Binds
    abstract fun errorPresenter(errorPresenter: ErrorPresenter): Error.Presenter

    @Binds
    abstract fun errorHandler(errorHandler: ErrorHandler): Error.Handler

    @ContributesAndroidInjector
    abstract fun errorDialogFragment(): ErrorDialogFragment

}