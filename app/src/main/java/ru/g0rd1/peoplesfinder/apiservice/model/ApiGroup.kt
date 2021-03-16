package ru.g0rd1.peoplesfinder.apiservice.model

import com.google.gson.annotations.SerializedName
import ru.g0rd1.peoplesfinder.common.enums.GroupType

data class ApiGroup(

    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("deactivated")
    val deactivated: String?,

    @SerializedName("photo_200")
    val photo: String?,

    @SerializedName("type")
    val type: GroupType,

    @SerializedName("members_count")
    val membersCount: Int

)