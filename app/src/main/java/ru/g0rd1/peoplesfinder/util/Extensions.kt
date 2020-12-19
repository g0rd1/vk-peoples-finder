package ru.g0rd1.peoplesfinder.util

fun <T> List<T>.parts(count: Int): List<List<T>> {
    val result: List<MutableList<T>> = (0 until count).map { mutableListOf() }
    this.forEachIndexed { index, element ->
        result[index % count].add(element)
    }
    return result
}