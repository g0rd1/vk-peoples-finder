package ru.g0rd1.peoplesfinder.model

import com.google.gson.annotations.SerializedName

data class Group(

    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("deactivated")
    val deactivated: String?,

    @SerializedName("photo_200")
    val photo: String?,

    @SerializedName("type")
    val type: String,

    @SerializedName("members_count")
    val membersCount: Int

) {
    class Type {
        companion object {
            const val GROUP = "group"
            const val PAGE = "page"
            const val EVENT = "event"
        }
    }
}