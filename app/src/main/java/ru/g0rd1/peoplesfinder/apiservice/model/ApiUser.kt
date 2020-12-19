package ru.g0rd1.peoplesfinder.apiservice.model

import com.google.gson.annotations.SerializedName
import ru.g0rd1.peoplesfinder.common.UserCity
import ru.g0rd1.peoplesfinder.common.UserLastSeen
import ru.g0rd1.peoplesfinder.common.UserRelation
import ru.g0rd1.peoplesfinder.common.UserSex

data class ApiUser(

    @SerializedName("id")
    val id: Int,

    @SerializedName("first_name")
    val firstName: String,

    @SerializedName("last_name")
    val lastName: String,

    @SerializedName("deactivated")
    val deactivated: String?,

    @SerializedName("is_closed")
    val isClosed: Boolean,

    @SerializedName("bdate")
    val birthDate: String?,

    @SerializedName("city")
    val city: UserCity?,

    @SerializedName("sex")
    val sex: UserSex?,

    @SerializedName("photo_200")
    val photo: String?,

    @SerializedName("last_seen")
    val lastSeen: UserLastSeen?,

    @SerializedName("relation")
    val relation: UserRelation?

)