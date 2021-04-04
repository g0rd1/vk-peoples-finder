package ru.g0rd1.peoplesfinder.ui.synchronization

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.g0rd1.peoplesfinder.databinding.FragmentSynchronizationBinding
import timber.log.Timber

@AndroidEntryPoint
class SynchronizationFragment : Fragment() {

    private val viewModel: SynchronizationViewModel by viewModels()

    lateinit var binding: FragmentSynchronizationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSynchronizationBinding.inflate(inflater, container, false)
        binding.vm = viewModel
        return binding.root
    }

    override fun onStart() {
        Timber.d("onStart()")
        super.onStart()
        viewModel.onStart()
    }

    companion object {
        fun create() = SynchronizationFragment()
    }

}