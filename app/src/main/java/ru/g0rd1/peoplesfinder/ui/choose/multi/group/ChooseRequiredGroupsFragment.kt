package ru.g0rd1.peoplesfinder.ui.choose.multi.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.DaggerDialogFragment
import ru.g0rd1.peoplesfinder.databinding.DialogMultichooseBinding
import ru.g0rd1.peoplesfinder.model.Group
import ru.g0rd1.peoplesfinder.ui.choose.multi.MultichooseItemAdapter
import javax.inject.Inject

class ChooseRequiredGroupsFragment : DaggerDialogFragment() {

    @Inject
    lateinit var viewModel: ChooseRequiredGroupsViewModel

    lateinit var binding: DialogMultichooseBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogMultichooseBinding.inflate(inflater, container, false)
        binding.searchResults.adapter = MultichooseItemAdapter<Group>()
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