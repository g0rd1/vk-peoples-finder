package ru.g0rd1.peoplesfinder.repo.app

interface VKAccessRepo {

    fun setUserToken(token: String)
    fun getUserToken(): String

    fun setUserId(id: String)
    fun getUserId(): String

}