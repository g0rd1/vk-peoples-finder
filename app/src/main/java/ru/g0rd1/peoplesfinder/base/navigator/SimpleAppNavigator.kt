package ru.g0rd1.peoplesfinder.base.navigator

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ru.g0rd1.peoplesfinder.R
import ru.g0rd1.peoplesfinder.base.MainActivityProvider
import ru.g0rd1.peoplesfinder.ui.authorization.AuthorizationFragment
import ru.g0rd1.peoplesfinder.ui.groups.GroupsFragment
import ru.g0rd1.peoplesfinder.ui.results.ResultsFragment
import ru.g0rd1.peoplesfinder.ui.settings.SettingsFragment
import ru.g0rd1.peoplesfinder.ui.synchronization.SynchronizationFragment
import java.util.*
import javax.inject.Inject

class SimpleAppNavigator @Inject constructor(
    private val mainActivityProvider: MainActivityProvider
) : AppNavigator {

    private val fragments: Deque<Fragment> = ArrayDeque()

    private val onFragmentChangeListeners: MutableList<(Fragment) -> Unit> = mutableListOf()

    private val fragmentManager: FragmentManager
        get() = mainActivityProvider.getMainActivity().supportFragmentManager

    private var currentFragment: Fragment? = null

    private fun showFragment(fragment: Fragment) {
        fragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commitAllowingStateLoss()
        currentFragment = fragment
        onFragmentChangeListeners.forEach { it(fragment) }
    }

    override fun authorization() {
        showFragment(AuthorizationFragment.create())
    }

    override fun synchronization() {
        showFragment(SynchronizationFragment.create())
    }

    override fun groups() {
        if (currentFragment is GroupsFragment) return
        val fragment = fragments.firstOrNull { it is GroupsFragment }?.also { fragments.remove(it) }
            ?: GroupsFragment.create()
        showFragment(fragment)
        fragments.addLast(fragment)
    }

    override fun settings() {
        if (currentFragment is SettingsFragment) return
        val fragment =
            fragments.firstOrNull { it is SettingsFragment }?.also { fragments.remove(it) }
                ?: SettingsFragment.create()
        showFragment(fragment)
        fragments.addLast(fragment)
    }

    override fun results() {
        if (currentFragment is ResultsFragment) return
        val fragment =
            fragments.firstOrNull { it is ResultsFragment }?.also { fragments.remove(it) }
                ?: ResultsFragment.create()
        showFragment(fragment)
        fragments.addLast(fragment)
    }

    override fun back() {
        val lastFragment = fragments.pollLast()
        if (lastFragment != null) {
            showFragment(lastFragment)
        } else {
            mainActivityProvider.getMainActivity().finish()
        }
    }

    override fun addOnFragmentChangeListener(listener: (Fragment) -> Unit) {
        currentFragment?.let { listener(it) }
        onFragmentChangeListeners.add(listener)
    }

    override fun removeOnFragmentChangeListener(listener: (Fragment) -> Unit) {
        onFragmentChangeListeners.remove(listener)
    }

}
