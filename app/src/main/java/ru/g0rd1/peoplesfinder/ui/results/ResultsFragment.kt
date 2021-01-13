package ru.g0rd1.peoplesfinder.ui.results

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.DaggerFragment
import ru.g0rd1.peoplesfinder.databinding.FragmentResultsBinding
import javax.inject.Inject

class ResultsFragment : DaggerFragment() {

    @Inject
    lateinit var viewModel: ResultsViewModel

    lateinit var binding: FragmentResultsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResultsBinding.inflate(inflater, container, false)
        binding.users.adapter = ResultsAdapter()
        binding.vm = viewModel
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

    companion object {
        fun create() = ResultsFragment()
    }

}