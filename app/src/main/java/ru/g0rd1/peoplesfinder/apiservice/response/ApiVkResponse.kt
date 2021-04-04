package ru.g0rd1.peoplesfinder.apiservice.response

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

@Suppress("unused")
class ApiVkResponse<T>(

    @SerializedName("error")
    val error: ApiVkError?,

    @SerializedName("execute_errors")
    val executeErrors: List<ApiVkError>?,

    @SerializedName("response")
    val rawResponse: JsonElement?,

    ) {

    class ApiResponse<T>(
        @SerializedName("items")
        val items: List<T>,
        @SerializedName("count")
        val count: Int,
    )

}