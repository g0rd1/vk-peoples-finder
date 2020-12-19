package ru.g0rd1.peoplesfinder.di.fragment

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.g0rd1.peoplesfinder.di.ViewModelKey
import ru.g0rd1.peoplesfinder.ui.groups.GroupsViewModel

@Module
abstract class GroupsFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(GroupsViewModel::class)
    abstract fun viewModel(viewModel: GroupsViewModel): ViewModel

}