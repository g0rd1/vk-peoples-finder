package ru.g0rd1.peoplesfinder.ui.groups.other

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.g0rd1.peoplesfinder.databinding.FragmentGroupsOtherBinding
import ru.g0rd1.peoplesfinder.ui.groups.GroupsAdapter
import ru.g0rd1.peoplesfinder.ui.groups.other.choose.ChooseOtherGroupsFragment
import timber.log.Timber

@AndroidEntryPoint
class OtherGroupsFragment : Fragment() {

    private val viewModel: OtherGroupsViewModel by viewModels()

    lateinit var binding: FragmentGroupsOtherBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentGroupsOtherBinding.inflate(inflater, container, false)
        binding.groups.adapter = GroupsAdapter()
        binding.vm = viewModel
        return binding.root
    }

    override fun onStart() {
        Timber.d("onStart()")
        super.onStart()
        observe()
        viewModel.onStart()
    }

    private fun observe() {
        viewModel.showAddGroup.observe(viewLifecycleOwner) {
            ChooseOtherGroupsFragment.create().show(childFragmentManager, null)
        }
    }

    companion object {
        fun create() = OtherGroupsFragment()
    }

}