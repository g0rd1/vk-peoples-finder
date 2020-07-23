package ru.g0rd1.peoplesfinder.apiservice.response

import ru.g0rd1.peoplesfinder.model.Group

data class GetGroupsResponse(val response: Response?, override val error: VkError?) : VkResponse {

    data class Response(
        val count: Int,
        val items: List<Group>
    )

}