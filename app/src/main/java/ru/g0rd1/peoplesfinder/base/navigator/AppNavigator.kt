package ru.g0rd1.peoplesfinder.base.navigator

import androidx.navigation.NavController
import ru.g0rd1.peoplesfinder.R
import javax.inject.Inject

class AppNavigator @Inject constructor() : Navigator {

    private lateinit var navigator: NavController

    override fun setNavigator(navController: NavController) {
        navigator = navController
    }

    override fun authorize() {
        navigator.navigate(R.id.login_fragment)
    }

    override fun groups() {
        navigator.navigate(R.id.groups_fragment)
    }

}