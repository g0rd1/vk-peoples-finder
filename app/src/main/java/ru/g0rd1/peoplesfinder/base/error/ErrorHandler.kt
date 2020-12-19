package ru.g0rd1.peoplesfinder.base.error

import ru.g0rd1.peoplesfinder.base.main.MainActivity
import timber.log.Timber
import javax.inject.Inject

class ErrorHandler @Inject constructor(
    mainActivity: MainActivity
) : Error.Handler {

    private val fragmentManager = mainActivity.supportFragmentManager

    private var currentErrorDialogFragment: ErrorDialogFragment? = null

    override fun handle(e: Throwable) {
        handle(e, null)
    }

    override fun handle(e: Throwable, retry: (() -> Unit)?) {
        Timber.e(e)
        currentErrorDialogFragment?.dismiss()
        val message = "Ошибка: ${e.localizedMessage}"
        val errorDialogFragment = ErrorDialogFragment.create(message).apply {
            retry?.let { setRetry(it) }
        }
        currentErrorDialogFragment = errorDialogFragment
        currentErrorDialogFragment?.show(fragmentManager, null)
    }

    override fun clear() {
        currentErrorDialogFragment?.dismiss()
        currentErrorDialogFragment = null
    }
}