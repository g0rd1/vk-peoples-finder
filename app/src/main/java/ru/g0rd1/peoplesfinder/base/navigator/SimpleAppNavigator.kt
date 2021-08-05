package ru.g0rd1.peoplesfinder.base.navigator

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ru.g0rd1.peoplesfinder.R
import ru.g0rd1.peoplesfinder.base.MainActivityProvider
import ru.g0rd1.peoplesfinder.ui.authorization.AuthorizationFragment
import ru.g0rd1.peoplesfinder.ui.groups.GroupsFragment
import ru.g0rd1.peoplesfinder.ui.lists.ListsFragment
import ru.g0rd1.peoplesfinder.ui.settings.SettingsFragment
import ru.g0rd1.peoplesfinder.ui.synchronization.SynchronizationFragment
import ru.g0rd1.peoplesfinder.ui.userDetail.UserDetailDialog
import ru.g0rd1.peoplesfinder.ui.userDetail.UserDetailDialogType
import ru.g0rd1.peoplesfinder.util.exhaustive
import java.util.*
import javax.inject.Inject

class SimpleAppNavigator @Inject constructor(
    private val mainActivityProvider: MainActivityProvider,
) : AppNavigator {

    private val rootFragments: Deque<Fragment> = ArrayDeque()

    private val fragments: Deque<Fragment> = ArrayDeque()

    private val onFragmentChangeListeners: MutableList<(Fragment) -> Unit> = mutableListOf()

    private val fragmentManager: FragmentManager
        get() = mainActivityProvider.getMainActivity().supportFragmentManager

    private var currentFragment: Fragment? = null

    private fun replaceFragment(fragment: Fragment) {
        val currentFragment = this.currentFragment
        if (currentFragment != null && currentFragment.javaClass == fragment.javaClass) return
        val fragmentToShow = rootFragments
            .firstOrNull { it.javaClass == fragment.javaClass }
            ?.also { rootFragments.remove(it) } ?: fragment
        fragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragmentToShow)
            .commit()
        this.currentFragment = fragmentToShow
        onFragmentChangeListeners.forEach { it(fragmentToShow) }
        rootFragments.addLast(fragment)
    }

    override fun showFragment(fragment: Fragment) {
        fragmentManager
            .beginTransaction()
            .add(R.id.fragment_container, fragment)
            .commit()
        fragments.addLast(fragment)
    }

    override fun authorization() {
        replaceFragment(AuthorizationFragment.create())
    }

    override fun synchronization() {
        replaceFragment(SynchronizationFragment.create())
    }

    override fun groups() {
        replaceFragment(GroupsFragment.create())
    }

    override fun settings() {
        replaceFragment(SettingsFragment.create())
    }

    override fun userDetail() {
        replaceFragment(UserDetailDialog.create(UserDetailDialogType.Multiple))
    }

    override fun lists() {
        replaceFragment(ListsFragment.create())
    }

    override fun back() {
        when {
            fragments.isNotEmpty() -> removeFragment(fragments.pollLast()!!)
            rootFragments.isNotEmpty() -> replaceFragment(rootFragments.pollLast()!!)
            else -> mainActivityProvider.getMainActivity().finish()
        }.exhaustive
    }

    private fun removeFragment(fragment: Fragment) {
        fragmentManager
            .beginTransaction()
            .remove(fragment)
            .commit()
    }

    override fun addOnFragmentChangeListener(listener: (Fragment) -> Unit) {
        currentFragment?.let { listener(it) }
        onFragmentChangeListeners.add(listener)
    }

    override fun removeOnFragmentChangeListener(listener: (Fragment) -> Unit) {
        onFragmentChangeListeners.remove(listener)
    }

}
