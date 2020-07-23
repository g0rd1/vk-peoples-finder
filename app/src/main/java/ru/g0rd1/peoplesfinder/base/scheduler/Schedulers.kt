package ru.g0rd1.peoplesfinder.base.scheduler

import io.reactivex.Scheduler

interface Schedulers {

    fun io(): Scheduler
    fun main(): Scheduler

}