package ru.g0rd1.peoplesfinder.apiservice.response

import com.google.gson.annotations.SerializedName

data class ApiVkError(
    @SerializedName("error_code")
    val code: Int,

    @SerializedName("error_msg")
    val message: String
) {

    object Code {
        const val TOO_MANY_REQUESTS_PER_SECOND = 6
        const val ACCESS_DENIED = 15
        const val RATE_LIMIT_REACHED = 29
    }

}