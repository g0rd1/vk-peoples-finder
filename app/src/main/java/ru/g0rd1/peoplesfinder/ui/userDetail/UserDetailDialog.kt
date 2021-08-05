package ru.g0rd1.peoplesfinder.ui.userDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.hilt.android.AndroidEntryPoint
import ru.g0rd1.peoplesfinder.base.FullScreenDialogFragment
import ru.g0rd1.peoplesfinder.databinding.DialogUserDetailBinding
import ru.g0rd1.peoplesfinder.ui.photosViewing.PhotosViewingFragment
import javax.inject.Inject

@AndroidEntryPoint
class UserDetailDialog: FullScreenDialogFragment() {

    @Inject
    lateinit var viewModelFactory: UserDetailViewModel.Factory

    lateinit var viewModel: UserDetailViewModel

    lateinit var binding: DialogUserDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userDialogType = requireArguments().getParcelable<UserDetailDialogType>(USER_DETAIL_DIALOG_TYPE)!!
        viewModel = viewModelFactory.create(userDialogType, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogUserDetailBinding.inflate(inflater, container, false)
        binding.info.sameGroups.adapter = SameGroupsAdapter()
        binding.vm = viewModel
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        observe()
        viewModel.onStart()
    }

    private fun observe() {
        viewModel.showPhotosEvent.observe(
            viewLifecycleOwner,
            observer@{ userId ->
                userId ?: return@observer
                PhotosViewingFragment.create(userId).show(childFragmentManager, null)
            }
        )
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