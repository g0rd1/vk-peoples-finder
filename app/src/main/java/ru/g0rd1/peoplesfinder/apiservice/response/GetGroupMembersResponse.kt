package ru.g0rd1.peoplesfinder.apiservice.response

import com.google.gson.annotations.SerializedName
import ru.g0rd1.peoplesfinder.model.User

data class GetGroupMembersResponse(
    val response: List<Response>?,

    @SerializedName("execute_errors")
    val executeErrors: List<VkError>?,

    override val error: VkError?
) : VkResponse {

    data class Response(
        /**
         * Количество членов группы
         */
        val count: Int,
        val items: List<User>
    )

}