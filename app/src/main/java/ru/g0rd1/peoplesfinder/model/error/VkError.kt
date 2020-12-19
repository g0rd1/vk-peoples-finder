package ru.g0rd1.peoplesfinder.model.error

sealed class VkError {
    data class Unknown(val code: Int) : VkError()
    object TooManyRequestsPerSecond : VkError()
    object RateLimitReached : VkError()
    object AccessDenied : VkError()
}
