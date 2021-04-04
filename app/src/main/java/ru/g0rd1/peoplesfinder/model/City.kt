package ru.g0rd1.peoplesfinder.model

import kotlinx.serialization.Serializable

@Serializable
data class City(
    val id: Int,
    val title: String,
    val important: Boolean
)
