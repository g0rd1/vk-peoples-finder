package ru.g0rd1.peoplesfinder.mapper

import ru.g0rd1.peoplesfinder.apiservice.response.ApiVkError
import ru.g0rd1.peoplesfinder.model.error.VkError
import javax.inject.Inject

class VkErrorMapper @Inject constructor() {

    fun transform(apiVkError: ApiVkError): VkError {
        return when (apiVkError.code) {
            ApiVkError.Code.ACCESS_DENIED -> VkError.AccessDenied(apiVkError.message)
            ApiVkError.Code.RATE_LIMIT_REACHED -> VkError.RateLimitReached(apiVkError.message)
            ApiVkError.Code.TOO_MANY_REQUESTS_PER_SECOND -> VkError.TooManyRequestsPerSecond(apiVkError.message)
            else -> VkError.Unknown(apiVkError.code)
        }
    }

}