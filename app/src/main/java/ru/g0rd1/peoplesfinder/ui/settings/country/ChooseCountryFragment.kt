package ru.g0rd1.peoplesfinder.ui.settings.country

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.g0rd1.peoplesfinder.base.FullScreenDialogFragment
import ru.g0rd1.peoplesfinder.databinding.DialogSingleChooseBinding
import ru.g0rd1.peoplesfinder.ui.choose.single.SingleChooseItemAdapter

@AndroidEntryPoint
class ChooseCountryFragment : FullScreenDialogFragment() {

    private val viewModel: ChooseCountryViewModel by viewModels()

    lateinit var binding: DialogSingleChooseBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogSingleChooseBinding.inflate(inflater, container, false)
        binding.searchResults.adapter = SingleChooseItemAdapter(viewModel)
        binding.vm = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
    }

    private fun observe() {
        viewModel.closeEvent.observe(
            viewLifecycleOwner,
            { dismiss() }
        )
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

    companion object {
        fun create() = ChooseCountryFragment()
    }

}