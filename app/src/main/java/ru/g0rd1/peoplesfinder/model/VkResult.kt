package ru.g0rd1.peoplesfinder.model

import ru.g0rd1.peoplesfinder.model.error.VkError

sealed class VkResult<T> {
    data class Success<T>(val data: T): VkResult<T>()
    sealed class Error<T>: VkResult<T>() {
        data class ApiVk<T>(val error: VkError): Error<T>()
        data class Generic<T>(val throwable: Throwable): Error<T>()
    }
}
