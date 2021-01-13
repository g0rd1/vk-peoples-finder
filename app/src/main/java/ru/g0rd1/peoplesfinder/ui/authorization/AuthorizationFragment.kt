package ru.g0rd1.peoplesfinder.ui.authorization

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.DaggerFragment
import ru.g0rd1.peoplesfinder.databinding.FragmentAuthorizationBinding
import timber.log.Timber
import javax.inject.Inject


class AuthorizationFragment : DaggerFragment() {

    @Inject
    lateinit var viewModel: AuthorizationViewModel

    lateinit var binding: FragmentAuthorizationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.d("TEST: onCreateView(")
        binding = FragmentAuthorizationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

    companion object {
        fun create() = AuthorizationFragment()
    }
}
