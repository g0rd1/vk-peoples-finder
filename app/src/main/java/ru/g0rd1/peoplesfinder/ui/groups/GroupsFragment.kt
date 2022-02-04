package ru.g0rd1.peoplesfinder.ui.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import ru.g0rd1.peoplesfinder.R
import ru.g0rd1.peoplesfinder.databinding.FragmentGroupsBinding
import ru.g0rd1.peoplesfinder.ui.groups.other.OtherGroupsFragment
import ru.g0rd1.peoplesfinder.ui.groups.users.UsersGroupsFragment
import timber.log.Timber

@AndroidEntryPoint
class GroupsFragment : Fragment() {

    private val viewModel: GroupsViewModel by viewModels()

    lateinit var binding: FragmentGroupsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentGroupsBinding.inflate(inflater, container, false)
        binding.vm = viewModel
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        Timber.d("onStart()")
        viewModel.onStart()
        childFragmentManager
            .beginTransaction()
            .replace(R.id.groups_fragment_container, UsersGroupsFragment.create())
            .commit()
        binding.tabs.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    val fragmentToShow = when (tab?.position) {
                        0 -> UsersGroupsFragment.create()
                        1 -> OtherGroupsFragment.create()
                        else -> throw IllegalStateException()
                    }
                    childFragmentManager
                        .beginTransaction()
                        .replace(R.id.groups_fragment_container, fragmentToShow)
                        .commit()
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabReselected(tab: TabLayout.Tab?) {}
            }
        )
    }

    companion object {
        fun create() = GroupsFragment()
    }

}