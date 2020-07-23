package ru.g0rd1.peoplesfinder.di.activity

import dagger.Binds
import dagger.Module
import ru.g0rd1.peoplesfinder.base.main.MainContract
import ru.g0rd1.peoplesfinder.base.main.MainPresenter

@Module
abstract class MainActivityModule {

    @Binds
    abstract fun presenter(presenter: MainPresenter): MainContract.Presenter

}