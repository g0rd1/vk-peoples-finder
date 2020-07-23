package ru.g0rd1.peoplesfinder.base.scheduler

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class AppSchedulers @Inject constructor() : Schedulers {

    override fun io(): Scheduler = io.reactivex.schedulers.Schedulers.io()

    override fun main(): Scheduler = AndroidSchedulers.mainThread()

}