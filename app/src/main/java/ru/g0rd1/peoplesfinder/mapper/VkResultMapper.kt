package ru.g0rd1.peoplesfinder.mapper

import ru.g0rd1.peoplesfinder.apiservice.response.ApiVkResult
import ru.g0rd1.peoplesfinder.model.VkResult
import javax.inject.Inject

class VkResultMapper @Inject constructor(
    private val vkErrorMapper: VkErrorMapper
) {

    fun <T, K> transform(
        apiVkResult: ApiVkResult<T>,
        transform: (T) -> K
    ): VkResult<K> {
        return when (apiVkResult) {
            is ApiVkResult.Error.ApiVk -> VkResult.Error.ApiVk(vkErrorMapper.transform(apiVkResult.error))
            is ApiVkResult.Error.Generic -> VkResult.Error.Generic(apiVkResult.throwable)
            is ApiVkResult.Success -> VkResult.Success(transform(apiVkResult.data))
        }
    }

}