package ru.g0rd1.peoplesfinder.common

import com.google.gson.annotations.SerializedName

enum class GroupType(val value: String) {
    @SerializedName("group")
    GROUP("group"),

    @SerializedName("page")
    PAGE("page"),

    @SerializedName("event")
    EVENT("event"),
}
