package ru.g0rd1.peoplesfinder.apiservice.response

import ru.g0rd1.peoplesfinder.apiservice.model.ApiUser

data class GetUserResponse(
    val response: List<ApiUser>
)