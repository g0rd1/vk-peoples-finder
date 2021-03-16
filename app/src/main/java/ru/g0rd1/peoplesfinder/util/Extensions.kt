package ru.g0rd1.peoplesfinder.util

import android.os.Bundle

val <T> T.exhaustive: T
    get() = this

fun <T> List<T>.parts(count: Int): List<List<T>> {
    val result: List<MutableList<T>> = (0 until count).map { mutableListOf() }
    this.forEachIndexed { index, element ->
        result[index % count].add(element)
    }
    return result
}

fun <T : Enum<T>> Bundle.putEnum(key: String, value: T) {
    this.putString(key, value.name)
}

@Suppress("UNCHECKED_CAST")
fun <T : Enum<T>> Bundle.getEnumOrNull(key: String): T? {
    val name = this.getString(key, null) ?: return null
    val value: Enum<T> = safeValueOf(name)
    return value as T
}

@Suppress("UNCHECKED_CAST")
fun <T : Enum<T>> Bundle.getEnum(key: String, defValue: T): T {
    val name = this.getString(key, null) ?: return defValue
    val value: Enum<T> = safeValueOf(name)
    return value as T
}

@Suppress("UNCHECKED_CAST")
fun <T : Enum<T>> Bundle.getEnum(key: String): T {
    val name = this.getString(key, null)!!
    val value: Enum<T> = safeValueOf(name)
    return value as T
}

inline fun <reified T : Enum<T>> safeValueOf(name: String): T {
    return java.lang.Enum.valueOf(T::class.java, name)
}