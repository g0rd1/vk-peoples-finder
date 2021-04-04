package ru.g0rd1.peoplesfinder.ui.photosViewing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.g0rd1.peoplesfinder.base.FullScreenDialogFragment
import ru.g0rd1.peoplesfinder.databinding.FragmentPhotosViewingBinding
import javax.inject.Inject

@AndroidEntryPoint
class PhotosViewingFragment : FullScreenDialogFragment() {

    @Inject
    lateinit var viewModelFactory: PhotosViewingViewModel.Factory

    val viewModel: PhotosViewingViewModel by viewModels {
        PhotosViewingViewModel.provideFactory(viewModelFactory, requireArguments().getInt(USER_ID))
    }

    lateinit var binding: FragmentPhotosViewingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPhotosViewingBinding.inflate(inflater, container, false)
        binding.vm = viewModel
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        observe()
        viewModel.onStart()
    }

    fun observe() {
        viewModel.getPhotosEvent.observe(
            viewLifecycleOwner,
            observer@{ photoUrls ->
                photoUrls ?: return@observer
                binding.viewPager.adapter = PhotoAdapter(requireActivity(), photoUrls)
            }
        )
        viewModel.closeEvent.observe(viewLifecycleOwner, { dismiss() })
    }

    companion object {

        private const val USER_ID = "userId"

        fun create(userId: Int) = PhotosViewingFragment().apply {
            arguments = Bundle().apply {
                putInt(USER_ID, userId)
            }
        }
    }

}