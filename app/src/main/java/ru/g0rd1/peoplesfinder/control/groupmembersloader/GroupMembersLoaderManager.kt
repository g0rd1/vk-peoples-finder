package ru.g0rd1.peoplesfinder.control.groupmembersloader

import io.reactivex.Observable

interface GroupMembersLoaderManager {

    fun reload()
    fun startOrContinue()
    fun pause()
    fun cancel()
    fun observeLoadStatus(): Observable<Status>

    sealed class Status {
        object Initial : Status()
        object Load : Status()
        object Pause : Status()
        object Finish : Status()
        object CommandProcessing : Status()
        sealed class Error : Status() {
            object RateLimitReached : Error()
            data class Generic(val throwable: Throwable) : Error()
        }
    }

}