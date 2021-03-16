package ru.g0rd1.peoplesfinder.repo.vk

import io.reactivex.Single
import ru.g0rd1.peoplesfinder.apiservice.ApiClient
import ru.g0rd1.peoplesfinder.apiservice.model.ApiCity
import ru.g0rd1.peoplesfinder.apiservice.model.ApiCountry
import ru.g0rd1.peoplesfinder.apiservice.model.ApiGroup
import ru.g0rd1.peoplesfinder.apiservice.model.ApiUser
import ru.g0rd1.peoplesfinder.apiservice.response.ApiVkError
import ru.g0rd1.peoplesfinder.apiservice.response.ApiVkResponse
import ru.g0rd1.peoplesfinder.apiservice.response.ApiVkResult
import javax.inject.Inject

class HttpVkRepo @Inject constructor(
    private val apiClient: ApiClient
): VkRepo {

    override fun getGroups(userId: Int, offset: Int, count: Int): Single<ApiVkResult<List<ApiGroup>>> {
        return apiClient.getGroups(
            userId = userId,
            offset = offset,
            count = count,
        ).toVkResult()
    }

    override fun getGroupMembers(code: String): Single<ApiVkResult<List<ApiUser>>> {
        return apiClient.getGroupMembers(code = code).toVkResult()
    }

    override fun getCountries(): Single<ApiVkResult<List<ApiCountry>>> {
        return apiClient.getCountries().toVkResult()
    }

    override fun getCities(count: Int, countryId: Int?, query: String?): Single<ApiVkResult<List<ApiCity>>> {
        return apiClient.getCities(
            count = count,
            countryId = countryId,
            query = query
        ).toVkResult()
    }

    private fun <T> Single<ApiVkResponse<T>>.toVkResult(): Single<ApiVkResult<List<T>>> {
        return this
            .flatMap { response ->
                when {
                    response.error != null -> {
                        if (response.error.code == ApiVkError.Code.TOO_MANY_REQUESTS_PER_SECOND) {
                            this.toVkResult()
                        } else {
                            Single.just(ApiVkResult.Error.ApiVk(response.error))
                        }
                    }
                    response.executeErrors?.isNotEmpty() == true -> {
                        val error = response.executeErrors.first()
                        if (error.code == ApiVkError.Code.TOO_MANY_REQUESTS_PER_SECOND) {
                            this.toVkResult()
                        } else {
                            Single.just(ApiVkResult.Error.ApiVk(error))
                        }
                    }
                    response.rawResponse != null -> {
                        Single.just(ApiVkResult.Success(response.getItems()))
                    }
                    else -> {
                        Single.just(ApiVkResult.Error.Generic(Throwable("Произошла неизвестная ошибка.")))
                    }
                }
            }
            .onErrorReturn {
                ApiVkResult.Error.Generic(it)
            }
    }

}