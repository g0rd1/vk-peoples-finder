package ru.g0rd1.peoplesfinder.ui.groups

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import dagger.android.support.DaggerFragment
import ru.g0rd1.peoplesfinder.R
import ru.g0rd1.peoplesfinder.base.main.MainActivity
import ru.g0rd1.peoplesfinder.databinding.FragmentGroupsBinding
import javax.inject.Inject

class GroupsFragment : DaggerFragment(), GroupsContract.View {

    @Inject
    lateinit var presenter: GroupsContract.Presenter

    @Inject
    lateinit var groupsAdapter: GroupsAdapter

    var binding: FragmentGroupsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGroupsBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        groupsAdapter.setOnItemClickListener(presenter::onGroupClick)
        binding!!.groups.adapter = groupsAdapter
        binding!!.startOrPauseButton.setOnClickListener { presenter.onLoadOrPauseButtonClick() }
        presenter.setView(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.groups_menu, menu)
        val item: MenuItem = menu.findItem(R.id.groups_app_bar_search)
        val themedContext = (activity as MainActivity).supportActionBar!!.themedContext
        val searchView = SearchView(themedContext)
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_IF_ROOM)
        item.actionView = searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                presenter.onQueryTextChange(newText)
                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
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

    override fun setGroups(groups: List<GroupView>) {
        groupsAdapter.setItems(groups)
    }

    override fun showContent() {
        setContentVisibility(View.VISIBLE)
    }

    override fun hideContent() {
        setContentVisibility(View.INVISIBLE)
    }

    private fun setContentVisibility(visibility: Int) {
        binding?.groups?.visibility = visibility
        binding?.startOrPauseButton?.visibility = visibility
    }

    override fun showLoader() {
        binding?.loader?.visibility = View.VISIBLE
    }

    override fun hideLoader() {
        binding?.loader?.visibility = View.INVISIBLE
    }

    override fun setGroupsView(groupsView: GroupsView) {
        binding?.groupsView = groupsView
    }

}
