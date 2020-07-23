package ru.g0rd1.peoplesfinder.di.fragment

import dagger.Binds
import dagger.Module
import ru.g0rd1.peoplesfinder.ui.authorization.AuthorizationContract
import ru.g0rd1.peoplesfinder.ui.authorization.AuthorizationPresenter

@Module
abstract class AuthorizationFragmentModule {

    @Binds
    abstract fun presenter(presenter: AuthorizationPresenter): AuthorizationContract.Presenter

}