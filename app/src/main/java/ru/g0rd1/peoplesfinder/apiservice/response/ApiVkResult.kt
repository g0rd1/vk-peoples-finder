package ru.g0rd1.peoplesfinder.apiservice.response


sealed class ApiVkResult<T> {
    data class Success<T>(val data: T): ApiVkResult<T>()
    sealed class Error<T>: ApiVkResult<T>() {
        data class ApiVk<T>(val error: ApiVkError): Error<T>()
        data class Generic<T>(val throwable: Throwable): Error<T>()
    }
}
