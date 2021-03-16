package ru.g0rd1.peoplesfinder.common.enums

import com.google.gson.annotations.SerializedName

enum class GroupType {
    @SerializedName("group")
    GROUP,

    @SerializedName("page")
    PAGE,

    @SerializedName("event")
    EVENT,
}