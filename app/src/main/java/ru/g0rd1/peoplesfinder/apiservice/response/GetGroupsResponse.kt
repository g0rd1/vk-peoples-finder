package ru.g0rd1.peoplesfinder.apiservice.response

import ru.g0rd1.peoplesfinder.apiservice.model.ApiGroup

data class GetGroupsResponse(val response: Response?, override val error: ApiVkError?) :
    VkResponse {

    data class Response(
        val count: Int,
        val items: List<ApiGroup>
    )

}