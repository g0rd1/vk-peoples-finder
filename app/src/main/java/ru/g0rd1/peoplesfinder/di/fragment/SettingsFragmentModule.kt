package ru.g0rd1.peoplesfinder.di.fragment

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.g0rd1.peoplesfinder.di.ViewModelKey
import ru.g0rd1.peoplesfinder.ui.settings.SettingsViewModel

@Module
abstract class SettingsFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun settingsViewModel(viewModel: SettingsViewModel): ViewModel

}