package ru.g0rd1.peoplesfinder.ui.settings.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.g0rd1.peoplesfinder.base.FullScreenDialogFragment
import ru.g0rd1.peoplesfinder.databinding.DialogMultichooseBinding
import ru.g0rd1.peoplesfinder.ui.choose.multi.MultichooseItemAdapter

@AndroidEntryPoint
class ChooseRequiredGroupsFragment : FullScreenDialogFragment() {

    private val viewModel: ChooseRequiredGroupsViewModel by viewModels()

    lateinit var binding: DialogMultichooseBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogMultichooseBinding.inflate(inflater, container, false)
        binding.searchResults.adapter = MultichooseItemAdapter(viewModel)
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
        fun create() = ChooseRequiredGroupsFragment()
    }

}