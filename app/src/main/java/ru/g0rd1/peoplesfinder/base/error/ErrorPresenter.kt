package ru.g0rd1.peoplesfinder.base.error

import javax.inject.Inject

class ErrorPresenter @Inject constructor() : Error.Presenter {

    private lateinit var view: Error.View

    private var retry: (() -> Unit)? = null

    override fun setView(view: Error.View) {
        this.view = view
    }

    override fun setRetry(retry: (() -> Unit)?) {
        this.retry = retry
    }

    override fun onRetryButtonClick() {
        val retry =
            this.retry ?: throw IllegalStateException("Retry button must be gone if retry is null")
        retry()
        view.dismiss()
    }

    override fun onNotRetryButtonClick() {
        view.dismiss()
    }

    override fun onStart() {
        if (retry == null) {
            view.hideRetryButton()
        } else {
            view.showRetryButton()
        }
    }

}