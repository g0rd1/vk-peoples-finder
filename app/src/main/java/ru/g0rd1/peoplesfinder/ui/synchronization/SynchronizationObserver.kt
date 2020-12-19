package ru.g0rd1.peoplesfinder.ui.synchronization

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SynchronizationObserver @Inject constructor() {

    private val syncSubject: Subject<Unit> = BehaviorSubject.create()

    fun observe(): Observable<Unit> = syncSubject

    fun synchronized() {
        syncSubject.onNext(Unit)
    }

}