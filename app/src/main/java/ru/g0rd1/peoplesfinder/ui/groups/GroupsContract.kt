package ru.g0rd1.peoplesfinder.ui.groups

interface GroupsContract {

    interface View {
        fun setGroups(groups: List<GroupView>)
        fun showContent()
        fun hideContent()
        fun showLoader()
        fun hideLoader()
        fun setGroupsView(groupsView: GroupsView)
    }

    interface Presenter {
        fun setView(view: View)
        fun onStart()
        fun onStop()
        fun onQueryTextChange(newText: String?)
        fun onGroupClick(groupView: GroupView)
        fun onLoadOrPauseButtonClick()
        fun stopButtonClick()
    }

}