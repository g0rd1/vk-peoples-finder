package ru.g0rd1.peoplesfinder.ui.userDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.DaggerDialogFragment
import ru.g0rd1.peoplesfinder.databinding.DialogUserDetailBinding
import javax.inject.Inject

class UserDetailDialog: DaggerDialogFragment() {

    @Inject
    lateinit var viewModelFactory: UserDetailViewModel.Factory

    lateinit var viewModel: UserDetailViewModel

    lateinit var binding: DialogUserDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogUserDetailBinding.inflate(inflater, container, false)
        val userDetailDialogType: UserDetailDialogType = requireArguments().getParcelable(USER_DETAIL_DIALOG_TYPE)!!
        viewModel = viewModelFactory.create(userDetailDialogType)
        binding.info.sameGroups.adapter = SameGroupsAdapter()
        binding.vm = viewModel
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

    companion object {

        private const val USER_DETAIL_DIALOG_TYPE = "userDetailDialogType"

        fun create(userDetailDialogType: UserDetailDialogType): UserDetailDialog {
            return UserDetailDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(USER_DETAIL_DIALOG_TYPE, userDetailDialogType)
                }
            }
        }
    }

}