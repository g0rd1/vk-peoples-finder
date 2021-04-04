package ru.g0rd1.peoplesfinder.model

import kotlinx.serialization.Serializable

@Serializable
data class Country(
    val id: Int,
    val title: String
)