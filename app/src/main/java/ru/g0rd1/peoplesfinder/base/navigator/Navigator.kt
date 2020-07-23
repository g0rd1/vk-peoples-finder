package ru.g0rd1.peoplesfinder.base.navigator

import androidx.navigation.NavController

interface Navigator {

    fun setNavigator(navController: NavController)
    fun authorize()
    fun groups()

}