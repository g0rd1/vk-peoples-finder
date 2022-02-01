package ru.g0rd1.peoplesfinder.base

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SnackbarManager @Inject constructor(
    private val mainActivityProvider: MainActivityProvider,
) {

    private lateinit var showSnackBar: (String, Int) -> Unit

    fun setShowSnackbarFunction(showSnackBar: (String, Int) -> Unit) {
        this.showSnackBar = showSnackBar
    }

    fun showSnackbar(message: String, length: Int) {
        showSnackBar(message, length)
    }

}