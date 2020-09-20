package ru.g0rd1.peoplesfinder.di.fragment

import dagger.Binds
import dagger.Module
import ru.g0rd1.peoplesfinder.ui.results.ResultsContract
import ru.g0rd1.peoplesfinder.ui.results.ResultsPresenter

@Module
abstract class ResultsFragmentModule {

    @Binds
    abstract fun presenter(presenter: ResultsPresenter): ResultsContract.Presenter

}