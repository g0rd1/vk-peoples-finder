package ru.g0rd1.peoplesfinder.ui.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.DaggerFragment
import ru.g0rd1.peoplesfinder.databinding.FragmentGroupsBinding
import javax.inject.Inject

class GroupsFragment : DaggerFragment() {

    @Inject
    lateinit var viewModel: GroupsViewModel

    lateinit var binding: FragmentGroupsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupsBinding.inflate(inflater, container, false)
        binding.groups.adapter = GroupsAdapter()
        binding.vm = viewModel
        return binding.root
    }

}