package ru.g0rd1.peoplesfinder.base.timber

import android.util.Log
import timber.log.Timber

class ReleaseTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) return
        super.log(priority, tag, message, t)
    }
}