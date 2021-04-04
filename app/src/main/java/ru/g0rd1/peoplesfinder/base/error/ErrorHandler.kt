package ru.g0rd1.peoplesfinder.base.error

import dagger.hilt.android.scopes.ActivityRetainedScoped
import ru.g0rd1.peoplesfinder.base.MainActivityProvider
import timber.log.Timber
import javax.inject.Inject

class ErrorHandler @Inject constructor(
    mainActivityProvider: MainActivityProvider
) : Error.Handler {

    private val fragmentManager = mainActivityProvider.getMainActivity().supportFragmentManager

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