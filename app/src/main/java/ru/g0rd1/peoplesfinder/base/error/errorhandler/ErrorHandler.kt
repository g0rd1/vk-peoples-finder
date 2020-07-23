package ru.g0rd1.peoplesfinder.base.error.errorhandler

interface ErrorHandler {

    fun handle(e: Throwable)
    fun handle(e: Throwable, retry: (() -> Unit)?)
    fun clear()

}