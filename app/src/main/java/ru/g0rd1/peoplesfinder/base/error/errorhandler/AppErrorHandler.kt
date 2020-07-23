package ru.g0rd1.peoplesfinder.base.error.errorhandler

import ru.g0rd1.peoplesfinder.base.error.errorviewmanager.ErrorView
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppErrorHandler @Inject constructor(
    private val errorViewManager: ErrorView.Manager
) : ErrorHandler {

    override fun handle(e: Throwable) {
        handle(e, null)
    }

    override fun handle(e: Throwable, retry: (() -> Unit)?) {
        errorViewManager.setMessage("Ошибка: ${e.localizedMessage}")
        if (retry != null) {
            errorViewManager.showWithRetryButton()
            errorViewManager.setOnButtonClickAction {
                errorViewManager.hide()
                retry()
            }
        } else {
            errorViewManager.showWithoutRetryButton()
        }
    }

    override fun clear() {
        errorViewManager.hide()
    }

}