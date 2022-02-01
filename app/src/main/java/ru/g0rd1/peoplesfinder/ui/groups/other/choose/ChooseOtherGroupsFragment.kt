package ru.g0rd1.peoplesfinder.ui.groups.other.choose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.g0rd1.peoplesfinder.base.FullScreenDialogFragment
import ru.g0rd1.peoplesfinder.databinding.DialogSingleChooseBinding
import ru.g0rd1.peoplesfinder.ui.choose.single.SingleChooseItemAdapter

@AndroidEntryPoint
class ChooseOtherGroupsFragment : FullScreenDialogFragment() {

    private val viewModel: ChooseOtherGroupViewModel by viewModels()

    lateinit var binding: DialogSingleChooseBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
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
            viewLifecycleOwner
        ) { dismiss() }
        viewModel.showSnackbar.observe(viewLifecycleOwner) observeShowSnackbar@{
            if (it.isNullOrBlank()) return@observeShowSnackbar
            Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

    companion object {
        fun create() = ChooseOtherGroupsFragment()
    }

}