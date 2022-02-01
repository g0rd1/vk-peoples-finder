package ru.g0rd1.peoplesfinder.ui.groups.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.g0rd1.peoplesfinder.databinding.FragmentGroupsUsersBinding
import ru.g0rd1.peoplesfinder.ui.groups.GroupsAdapter
import timber.log.Timber

@AndroidEntryPoint
class UsersGroupsFragment : Fragment() {

    private val viewModel: UsersGroupsViewModel by viewModels()

    lateinit var binding: FragmentGroupsUsersBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentGroupsUsersBinding.inflate(inflater, container, false)
        binding.groups.adapter = GroupsAdapter()
        binding.vm = viewModel
        return binding.root
    }

    override fun onStart() {
        Timber.d("onStart()")
        super.onStart()
        viewModel.onStart()
    }

    companion object {
        fun create() = UsersGroupsFragment()
    }

}