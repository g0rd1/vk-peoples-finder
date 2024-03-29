package ru.g0rd1.peoplesfinder.di

import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.g0rd1.peoplesfinder.ui.userDetail.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FragmentsModule {

    companion object {

        @Provides
        @Singleton
        fun userDetailViewModelFactory(
            userDetailSingleViewModelFactory: UserDetailSingleViewModel.Factory,
            userDetailMultipleViewModelFactory: UserDetailMultipleViewModel.Factory,
        ): UserDetailViewModel.Factory {
            return object : UserDetailViewModel.Factory {
                override fun create(dialogType: UserDetailDialogType, fragment: UserDetailDialog): UserDetailViewModel {
                    return when (dialogType) {
                        UserDetailDialogType.Multiple -> userDetailMultipleViewModel(
                            fragment,
                            userDetailMultipleViewModelFactory,
                        )
                        is UserDetailDialogType.Single -> userDetailSingleViewModel(
                            fragment,
                            userDetailSingleViewModelFactory,
                            dialogType
                        )
                    }
                }
            }
        }

        private fun userDetailMultipleViewModel(
            fragment: UserDetailDialog,
            userDetailMultipleViewModelFactory: UserDetailMultipleViewModel.Factory,
        ): UserDetailMultipleViewModel {
            return fragment.createViewModelLazy(
                UserDetailMultipleViewModel::class,
                { fragment.viewModelStore },
                getUserDetailMultipleViewModelFactory(userDetailMultipleViewModelFactory)
            ).value.also {
                println()
            }
        }

        private fun userDetailSingleViewModel(
            fragment: UserDetailDialog,
            userDetailSingleViewModelFactory: UserDetailSingleViewModel.Factory,
            dialogType: UserDetailDialogType.Single,
        ): UserDetailSingleViewModel {
            return fragment.createViewModelLazy(
                UserDetailSingleViewModel::class,
                { fragment.viewModelStore },
                getUserDetailSingleViewModelFactory(userDetailSingleViewModelFactory, dialogType)
            ).value
        }

        private fun getUserDetailSingleViewModelFactory(
            userDetailSingleViewModelFactory: UserDetailSingleViewModel.Factory,
            dialogType: UserDetailDialogType.Single,
        ): () -> ViewModelProvider.Factory {
            return {
                UserDetailSingleViewModel.provideFactory(
                    userDetailSingleViewModelFactory,
                    dialogType.userId
                )
            }
        }

        private fun getUserDetailMultipleViewModelFactory(
            userDetailMultipleViewModelFactory: UserDetailMultipleViewModel.Factory,
        ): () -> ViewModelProvider.Factory {
            return {
                UserDetailMultipleViewModel.provideFactory(userDetailMultipleViewModelFactory)
            }
        }

    }

}