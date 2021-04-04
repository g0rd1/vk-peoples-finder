package ru.g0rd1.peoplesfinder.ui.photosViewing.photo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import ru.g0rd1.peoplesfinder.databinding.FragmentPhotoBinding

@AndroidEntryPoint
class PhotoFragment : Fragment() {

    lateinit var binding: FragmentPhotoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPhotoBinding.inflate(inflater, container, false)
        binding.photoUrl = requireArguments().getString(PHOTO_URL)!!
        return binding.root
    }

    companion object {

        private const val PHOTO_URL = "PHOTO_URL"

        fun create(photoUrl: String) = PhotoFragment().apply {
            arguments = Bundle().apply {
                putString(PHOTO_URL, photoUrl)
            }
        }
    }

}