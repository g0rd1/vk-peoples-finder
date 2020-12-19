package ru.g0rd1.peoplesfinder.ui.synchronization

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.DaggerFragment
import ru.g0rd1.peoplesfinder.databinding.FragmentSynchronizationBinding
import javax.inject.Inject

class SynchronizationFragment : DaggerFragment() {

    @Inject
    lateinit var viewModel: SynchronizationViewModel

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

}