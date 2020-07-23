package ru.g0rd1.peoplesfinder.base.error.errorviewmanager

import ru.g0rd1.peoplesfinder.databinding.ErrorBinding

interface ErrorView {

    interface Manager {
        fun showWithRetryButton()
        fun showWithoutRetryButton()
        fun hide()
        fun setMessage(message: String)
        fun setOnButtonClickAction(action: () -> Unit)
    }

    fun initiateManager(errorBinding: ErrorBinding)
    fun getManager(): Manager

}