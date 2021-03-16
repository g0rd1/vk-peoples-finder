package ru.g0rd1.peoplesfinder.apiservice.response

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken

class ApiVkResponse<T> (

    @SerializedName("error")
    val error: ApiVkError?,

    @SerializedName("execute_errors")
    val executeErrors: List<ApiVkError>?,

    @SerializedName("response")
    val rawResponse: JsonElement?

) {

    class ApiResponse<T> (
        @SerializedName("items")
        val items: List<T>,
        @SerializedName("count")
        val count: Int
    )

    fun getItems(): List<T> {
        return when (rawResponse) {
            is JsonObject -> {
                val type = object : TypeToken<ApiResponse<T>>() {}.type
                val apiResponse: ApiResponse<T> = Gson().fromJson(rawResponse, type)
                return apiResponse.items
            }
            is JsonArray -> {
                val type = object : TypeToken<List<ApiResponse<T>>>() {}.type
                val apiResponses: List<ApiResponse<T>> = Gson().fromJson(rawResponse, type)
                return apiResponses.flatMap { it.items }
            }
            else -> listOf()
        }
    }

}