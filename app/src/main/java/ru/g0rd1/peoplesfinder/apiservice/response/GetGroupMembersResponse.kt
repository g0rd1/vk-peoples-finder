package ru.g0rd1.peoplesfinder.apiservice.response

import com.google.gson.JsonArray
import com.google.gson.annotations.SerializedName
import ru.g0rd1.peoplesfinder.apiservice.model.ApiUser

data class GetGroupMembersResponse(
    @SerializedName("response")
    val rawResponse: JsonArray?,

    @SerializedName("execute_errors")
    val executeErrors: List<ApiVkError>?,

    override val error: ApiVkError?
) : VkResponse {

    data class Response(
        /**
         * Количество членов группы
         */
        val count: Int,
        val items: List<ApiUser>
    )

}