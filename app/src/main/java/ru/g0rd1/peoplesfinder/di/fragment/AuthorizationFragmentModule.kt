package ru.g0rd1.peoplesfinder.di.fragment

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.g0rd1.peoplesfinder.di.ViewModelKey
import ru.g0rd1.peoplesfinder.ui.authorization.AuthorizationViewModel

@Module
abstract class AuthorizationFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(AuthorizationViewModel::class)
    abstract fun authorizationViewModel(viewModel: AuthorizationViewModel): ViewModel

}