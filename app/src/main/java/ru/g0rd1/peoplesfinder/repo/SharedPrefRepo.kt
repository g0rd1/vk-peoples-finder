package ru.g0rd1.peoplesfinder.repo

interface SharedPrefRepo {

    fun putInt(key: String, value: Int)

    fun getInt(key: String, defValue: Int): Int

    fun putString(key: String, value: String)

    fun getStringOrNull(key: String): String?

    fun getString(key: String, defValue: String): String

    fun putBoolean(key: String, value: Boolean)

    fun getBoolean(key: String, defValue: Boolean = false): Boolean

    fun <T> putObject(key: String, value: T, clazz: Class<T>)

    fun <T> getObject(key: String, defValue: T, clazz: Class<T>): T

    fun <T> putList(key: String, value: List<T>, clazz: Class<T>)

    fun <T> getList(key: String, defValue: List<T> = listOf(), clazz: Class<T>): List<T>

    fun <T : Enum<T>> putEnum(key: String, value: T)

    fun <T : Enum<T>> getEnum(key: String, defaultValue: T): T

    fun <T : Enum<T>> getEnum(key: String, clazz: Class<T>): T
}