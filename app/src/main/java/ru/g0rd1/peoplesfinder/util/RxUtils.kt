package ru.g0rd1.peoplesfinder.util

import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun <T> Single<T>.observeOnUI(): Single<T> = this.observeOn(AndroidSchedulers.mainThread())

fun <T> Single<T>.subscribeOnIo(): Single<T> = this.subscribeOn(Schedulers.io())

fun <T> Maybe<T>.observeOnUI(): Maybe<T> = this.observeOn(AndroidSchedulers.mainThread())

fun <T> Maybe<T>.subscribeOnIo(): Maybe<T> = this.subscribeOn(Schedulers.io())

@Suppress("UNCHECKED_CAST")
fun <T, R> Iterable<Single<T>>.zip(function: (Iterable<T>) -> R): Single<R> =
    Single.zip(this) { function((it as Array<T>).asIterable()) }

fun <T> Observable<T>.observeOnUI(): Observable<T> = this.observeOn(AndroidSchedulers.mainThread())

fun <T> Observable<T>.subscribeOnIo(): Observable<T> = this.subscribeOn(Schedulers.io())

fun <T> Flowable<T>.observeOnUI(): Flowable<T> = this.observeOn(AndroidSchedulers.mainThread())

fun <T> Flowable<T>.subscribeOnIo(): Flowable<T> = this.subscribeOn(Schedulers.io())

fun Completable.observeOnUI(): Completable = this.observeOn(AndroidSchedulers.mainThread())

fun Completable.subscribeOnIo(): Completable = this.subscribeOn(Schedulers.io())