package ru.g0rd1.peoplesfinder.ui.authorization

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.g0rd1.peoplesfinder.databinding.FragmentAuthorizationBinding
import timber.log.Timber


@AndroidEntryPoint
class AuthorizationFragment : Fragment() {

    private val viewModel: AuthorizationViewModel by viewModels()

    private var binding: FragmentAuthorizationBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAuthorizationBinding.inflate(inflater, container, false)
        this.binding = binding
        binding.vm = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observe()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    private fun observe() {
        viewModel.reload.observe(
            viewLifecycleOwner,
            {
                binding?.webView?.reload()
            }
        )
    }

    override fun onStart() {
        Timber.d("onStart()")
        super.onStart()
        viewModel.onStart()
    }

    companion object {
        fun create() = AuthorizationFragment()
    }
}
