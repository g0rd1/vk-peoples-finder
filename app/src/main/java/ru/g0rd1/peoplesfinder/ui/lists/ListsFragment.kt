package ru.g0rd1.peoplesfinder.ui.lists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.g0rd1.peoplesfinder.databinding.FragmentListsBinding

@AndroidEntryPoint
class ListsFragment : Fragment() {

    private val viewModel: ListsViewModel by viewModels()

    lateinit var binding: FragmentListsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentListsBinding.inflate(inflater, container, false)
        binding.vm = viewModel
        binding.composeView.setContent {
            ListsView(
                listsViewData = viewModel.state.collectAsState(),
                onFavoriteTabClick = viewModel::onFavoriteClick,
                onBlockedTabClick = viewModel::onBlockedClick
            )
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

    companion object {
        fun create() = ListsFragment()
    }

}