package ru.g0rd1.peoplesfinder.base.navigator

import androidx.fragment.app.Fragment

interface AppNavigator {
    fun authorization()
    fun synchronization()
    fun groups()
    fun settings()
    fun userDetail()
    fun lists()
    fun back()
    fun showFragment(fragment: Fragment)
    fun addOnFragmentChangeListener(listener: (Fragment) -> Unit)
    fun removeOnFragmentChangeListener(listener: (Fragment) -> Unit)
}
