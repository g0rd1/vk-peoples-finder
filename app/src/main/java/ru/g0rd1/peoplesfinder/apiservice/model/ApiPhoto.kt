package ru.g0rd1.peoplesfinder.apiservice.model

import com.google.gson.annotations.SerializedName
import ru.g0rd1.peoplesfinder.common.enums.PhotoType

data class ApiPhoto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("album_id")
    val albumId: Int,
    @SerializedName("owner_id")
    val ownerId: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("text")
    val text: String,
    @SerializedName("date")
    val date: Int,
    @SerializedName("sizes")
    val sizes: List<ApiSize>,
) {

    data class ApiSize(
        @SerializedName("type")
        val type: PhotoType,
        @SerializedName("url")
        val url: String,
        @SerializedName("width")
        val width: Int,
        @SerializedName("height")
        val height: Int,
    )

}