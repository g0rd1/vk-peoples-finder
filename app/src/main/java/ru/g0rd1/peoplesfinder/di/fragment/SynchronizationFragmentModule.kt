package ru.g0rd1.peoplesfinder.di.fragment

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.g0rd1.peoplesfinder.di.ViewModelKey
import ru.g0rd1.peoplesfinder.ui.synchronization.SynchronizationViewModel

@Module
abstract class SynchronizationFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(SynchronizationViewModel::class)
    abstract fun synchronizationViewModel(viewModel: SynchronizationViewModel): ViewModel

}