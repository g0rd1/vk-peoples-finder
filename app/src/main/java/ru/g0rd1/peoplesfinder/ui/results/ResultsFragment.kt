package ru.g0rd1.peoplesfinder.ui.results

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.DaggerFragment
import ru.g0rd1.peoplesfinder.databinding.FragmentResultsBinding
import ru.g0rd1.peoplesfinder.db.entity.UserEntity
import javax.inject.Inject

class ResultsFragment : DaggerFragment(), ResultsContract.View {

    @Inject
    lateinit var presenter: ResultsContract.Presenter

    @Inject
    lateinit var usersAdapter: UsersAdapter

    var binding: FragmentResultsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentResultsBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding!!.users.adapter = usersAdapter
        presenter.setView(this)
    }

    override fun onStart() {
        super.onStart()
        presenter.onStart()
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun setUsers(users: List<UserEntity>) {
        usersAdapter.setItems(users)
    }

}