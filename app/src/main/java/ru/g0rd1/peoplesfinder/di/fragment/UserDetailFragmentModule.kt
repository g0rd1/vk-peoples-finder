package ru.g0rd1.peoplesfinder.di.fragment

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.g0rd1.peoplesfinder.di.ViewModelKey
import ru.g0rd1.peoplesfinder.ui.userDetail.UserDetailViewModel

@Module
abstract class UserDetailFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(UserDetailViewModel::class)
    abstract fun userDetailViewModel(viewModel: UserDetailViewModel): ViewModel

}