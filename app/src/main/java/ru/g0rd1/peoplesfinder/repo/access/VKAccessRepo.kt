package ru.g0rd1.peoplesfinder.repo.access

interface VKAccessRepo {

    fun setUserToken(token: String)
    fun getUserToken(): String

    fun setUserId(id: Int)
    fun getUserId(): Int

}