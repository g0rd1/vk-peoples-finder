package ru.g0rd1.peoplesfinder.apiservice.response

import ru.g0rd1.peoplesfinder.model.User

data class GetUserResponse(
    val response: List<User>
)