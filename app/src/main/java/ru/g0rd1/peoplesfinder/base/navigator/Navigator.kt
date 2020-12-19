package ru.g0rd1.peoplesfinder.base.navigator

import androidx.navigation.NavController

interface Navigator {

    fun setNavigator(
        navController: NavController,
        showNavigationMenu: (showNavigationMenu: Boolean) -> Unit
    )

    fun authorization()
    fun synchronization()
    fun groups()
}