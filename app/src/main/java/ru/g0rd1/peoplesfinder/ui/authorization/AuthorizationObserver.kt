package ru.g0rd1.peoplesfinder.ui.authorization

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthorizationObserver @Inject constructor() {

    private val statusSubject: Subject<Unit> = BehaviorSubject.create()

    fun observe(): Observable<Unit> = statusSubject

    fun authorized() {
        statusSubject.onNext(Unit)
    }

}