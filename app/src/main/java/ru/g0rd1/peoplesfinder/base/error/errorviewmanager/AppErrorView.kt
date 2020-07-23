package ru.g0rd1.peoplesfinder.base.error.errorviewmanager

import android.view.View
import ru.g0rd1.peoplesfinder.databinding.ErrorBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppErrorView @Inject constructor() : ErrorView {

    private lateinit var manager: ErrorView.Manager

    private var status: Status = Status.HIDE

    override fun initiateManager(errorBinding: ErrorBinding) {
        if (::manager.isInitialized) throw Exception("Manager is already initialized")
        manager = object : ErrorView.Manager {

            override fun showWithRetryButton() {
                if (status == Status.SHOWN) throw IllegalStateException("error view is already shown")
                errorBinding.root.visibility = View.VISIBLE
                errorBinding.retryButton.visibility = View.VISIBLE
                status = Status.SHOWN
            }

            override fun showWithoutRetryButton() {
                if (status == Status.SHOWN) throw IllegalStateException("error view is already shown")
                errorBinding.root.visibility = View.VISIBLE
                errorBinding.retryButton.visibility = View.INVISIBLE
                status = Status.SHOWN
            }

            override fun hide() {
                errorBinding.root.visibility = View.INVISIBLE
                status = Status.HIDE
            }

            override fun setMessage(message: String) {
                errorBinding.message.text = message
            }

            override fun setOnButtonClickAction(action: () -> Unit) {
                errorBinding.retryButton.setOnClickListener { action() }
            }
        }
    }

    override fun getManager(): ErrorView.Manager {
        if (!::manager.isInitialized) throw Exception("Manager is not initialized")
        return manager
    }

    enum class Status {
        SHOWN, HIDE
    }

}