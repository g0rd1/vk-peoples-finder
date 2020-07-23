package ru.g0rd1.peoplesfinder.di.fragment

import dagger.Binds
import dagger.Module
import ru.g0rd1.peoplesfinder.ui.groups.GroupsContract
import ru.g0rd1.peoplesfinder.ui.groups.GroupsPresenter

@Module
abstract class GroupsFragmentModule {

    @Binds
    abstract fun groupsPresenter(groupsPresenter: GroupsPresenter): GroupsContract.Presenter

}