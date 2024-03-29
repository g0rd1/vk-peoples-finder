package ru.g0rd1.peoplesfinder.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.g0rd1.peoplesfinder.databinding.FragmentSettingsBinding
import ru.g0rd1.peoplesfinder.ui.settings.city.ChooseCityFragment
import ru.g0rd1.peoplesfinder.ui.settings.country.ChooseCountryFragment
import ru.g0rd1.peoplesfinder.ui.settings.group.ChooseRequiredGroupsFragment

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModels()

    lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding.vm = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observe()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

    private fun observe() {
        viewModel.showChooseCityDialog.observe(
            viewLifecycleOwner,
            { ChooseCityFragment.create().show(childFragmentManager, null) }
        )
        viewModel.showChooseRequiredGroupsDialog.observe(
            viewLifecycleOwner,
            { ChooseRequiredGroupsFragment.create().show(childFragmentManager, null) }
        )
        viewModel.showChooseCountryDialog.observe(
            viewLifecycleOwner,
            { ChooseCountryFragment.create().show(childFragmentManager, null) }
        )
    }

    companion object {
        fun create() = SettingsFragment()
    }

}