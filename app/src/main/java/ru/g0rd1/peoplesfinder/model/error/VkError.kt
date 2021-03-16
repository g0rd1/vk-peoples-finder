package ru.g0rd1.peoplesfinder.model.error

sealed class VkError(open val message: String) {
    data class Unknown(val code: Int): VkError("Неизвестная ошибка")
    data class TooManyRequestsPerSecond(override val message: String) : VkError(message)
    data class RateLimitReached(override val message: String) : VkError(message)
    data class AccessDenied(override val message: String) : VkError(message)
}
