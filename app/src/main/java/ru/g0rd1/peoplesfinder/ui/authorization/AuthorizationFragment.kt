package ru.g0rd1.peoplesfinder.ui.authorization

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.DaggerFragment
import ru.g0rd1.peoplesfinder.databinding.FragmentAuthorizationBinding
import javax.inject.Inject


class AuthorizationFragment : DaggerFragment() {

    @Inject
    lateinit var viewModel: AuthorizationViewModel

    lateinit var binding: FragmentAuthorizationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthorizationBinding.inflate(inflater, container, false)
        return binding.root
    }

}
