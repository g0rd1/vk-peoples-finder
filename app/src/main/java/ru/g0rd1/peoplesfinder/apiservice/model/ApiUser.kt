package ru.g0rd1.peoplesfinder.apiservice.model

import com.google.gson.annotations.SerializedName
import ru.g0rd1.peoplesfinder.common.enums.Relation
import ru.g0rd1.peoplesfinder.common.enums.Sex

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
    val birthday: String?,

    @SerializedName("country")
    val country: ApiCountry?,

    @SerializedName("city")
    val city: ApiCity?,

    @SerializedName("sex")
    val sex: Sex?,

    @SerializedName("has_photo")
    val hasPhoto: Int,

    @SerializedName("relation")
    val relation: Relation?,

    @SerializedName("photo_100")
    val photo100: String?,

    @SerializedName("photo_max")
    val photoMax: String?

)