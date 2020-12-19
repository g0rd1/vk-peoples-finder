package ru.g0rd1.peoplesfinder.base.error

interface Error {

    interface Handler {
        fun handle(e: Throwable)
        fun handle(e: Throwable, retry: (() -> Unit)?)
        fun clear()
    }

    interface Presenter {
        fun setView(view: View)
        fun setRetry(retry: (() -> Unit)?)
        fun onRetryButtonClick()
        fun onNotRetryButtonClick()
        fun onStart()
    }

    interface View {
        fun showRetryButton()
        fun hideRetryButton()
        fun setRetry(retry: (() -> Unit))
        fun dismiss()
    }

}