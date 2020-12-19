package ru.g0rd1.peoplesfinder.common

import com.google.gson.annotations.SerializedName

data class UserLastSeen(

    @SerializedName("time")
    val time: Int,

    @SerializedName("platform")
    val platform: Platform

) {
    enum class Platform {

        @SerializedName("1")
        MOBILE,

        @SerializedName("2")
        IPHONE,

        @SerializedName("3")
        IPAD,

        @SerializedName("4")
        ANDROID,

        @SerializedName("5")
        WINDOWS_PHONE,

        @SerializedName("6")
        WINDOWS_10,

        @SerializedName("7")
        SITE
    }
}

data class UserCity(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String
)

enum class UserSex {

    @SerializedName("0")
    NOT_SPECIFIED,

    @SerializedName("1")
    FEMALE,

    @SerializedName("2")
    MALE
}

enum class UserRelation {
    @SerializedName("0")
    NOT_SPECIFIED,

    @SerializedName("1")
    SINGLE,

    @SerializedName("2")
    IN_A_RELATIONSHIP,

    @SerializedName("3")
    ENGAGED,

    @SerializedName("4")
    MARRIED,

    @SerializedName("5")
    IT_IS_COMPLICATED,

    @SerializedName("6")
    ACTIVELY_SEARCHING,

    @SerializedName("7")
    IN_LOVE,

    @SerializedName("8")
    IN_A_CIVIL_UNION
}

