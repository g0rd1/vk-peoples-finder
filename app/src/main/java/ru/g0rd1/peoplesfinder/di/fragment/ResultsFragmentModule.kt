package ru.g0rd1.peoplesfinder.di.fragment

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.g0rd1.peoplesfinder.di.ViewModelKey
import ru.g0rd1.peoplesfinder.ui.results.ResultsViewModel

@Module
abstract class ResultsFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(ResultsViewModel::class)
    abstract fun viewModel(viewModel: ResultsViewModel): ViewModel

}