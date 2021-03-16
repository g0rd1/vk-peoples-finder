package ru.g0rd1.peoplesfinder.apiservice.model

import com.google.gson.annotations.SerializedName

data class ApiCountry(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String
)