package ru.g0rd1.peoplesfinder.ui.choose.single.city

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.DaggerDialogFragment
import ru.g0rd1.peoplesfinder.databinding.DialogSingleChooseBinding
import ru.g0rd1.peoplesfinder.model.City
import ru.g0rd1.peoplesfinder.ui.choose.single.SingleChooseItemAdapter
import javax.inject.Inject

class ChooseCityFragment : DaggerDialogFragment() {

    @Inject
    lateinit var viewModel: ChooseCityViewModel

    lateinit var binding: DialogSingleChooseBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogSingleChooseBinding.inflate(inflater, container, false)
        binding.searchResults.adapter = SingleChooseItemAdapter<City>()
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
        fun create() = ChooseCityFragment()
    }

}