package ru.g0rd1.peoplesfinder.repo.vk

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import io.reactivex.Single
import ru.g0rd1.peoplesfinder.apiservice.ApiClient
import ru.g0rd1.peoplesfinder.apiservice.model.*
import ru.g0rd1.peoplesfinder.apiservice.response.ApiVkError
import ru.g0rd1.peoplesfinder.apiservice.response.ApiVkResponse
import ru.g0rd1.peoplesfinder.apiservice.response.ApiVkResult
import ru.g0rd1.peoplesfinder.util.subscribeOnIo
import javax.inject.Inject

class HttpVkRepo @Inject constructor(
    private val apiClient: ApiClient
): VkRepo {

    override fun getGroups(userId: Int, offset: Int, count: Int): Single<ApiVkResult<List<ApiGroup>>> {
        return apiClient.getGroups(
            userId = userId,
            offset = offset,
            count = count,
        ).toVkResult(ApiGroup::class.java).subscribeOnIo()
    }

    override fun searchGroups(searchText: String): Single<ApiVkResult<List<ApiGroup>>> {
        return apiClient.searchGroups(searchText).toVkResult(ApiGroup::class.java).subscribeOnIo()
    }

    override fun getGroupMembers(groupId: String, offset: Int, count: Int): Single<ApiVkResult<List<ApiUser>>> {
        return apiClient.getGroupMembers(groupId, offset, count).toVkResult(ApiUser::class.java).subscribeOnIo()
    }

    override fun getCountries(): Single<ApiVkResult<List<ApiCountry>>> {
        return apiClient.getCountries().toVkResult(ApiCountry::class.java).subscribeOnIo()
    }

    override fun getCities(
        count: Int,
        countryId: Int?,
        query: String?,
        needAll: Boolean,
    ): Single<ApiVkResult<List<ApiCity>>> {
        return apiClient.getCities(
            count = count,
            countryId = countryId,
            query = query,
            needAll = if (needAll) 1 else 0
        )
            .doFinally { print("123") }
            .toVkResult(ApiCity::class.java).subscribeOnIo()
    }

    override fun getProfilePhotos(ownerId: Int): Single<ApiVkResult<List<ApiPhoto>>> {
        return apiClient.getProfilePhotos(ownerId).toVkResult(ApiPhoto::class.java).subscribeOnIo()
    }

    private fun <T> Single<ApiVkResponse<T>>.toVkResult(type: Class<T>): Single<ApiVkResult<List<T>>> {
        return this
            .flatMap { response ->
                when {
                    response.error != null -> {
                        if (response.error.code == ApiVkError.Code.TOO_MANY_REQUESTS_PER_SECOND) {
                            this.toVkResult(type)
                        } else {
                            Single.just(ApiVkResult.Error.ApiVk(response.error))
                        }
                    }
                    response.executeErrors?.isNotEmpty() == true -> {
                        val error = response.executeErrors.first()
                        if (error.code == ApiVkError.Code.TOO_MANY_REQUESTS_PER_SECOND) {
                            this.toVkResult(type)
                        } else {
                            Single.just(ApiVkResult.Error.ApiVk(error))
                        }
                    }
                    response.rawResponse != null -> {
                        Single.just(ApiVkResult.Success(getItems(response.rawResponse, type)))
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

    private fun <T> getItems(rawResponse: JsonElement, parameterType: Class<T>): List<T> {
        return when (rawResponse) {
            is JsonObject -> {
                val type = TypeToken.getParameterized(ApiVkResponse.ApiResponse::class.java, parameterType).type
                val apiResponse: ApiVkResponse.ApiResponse<T> = Gson().fromJson(rawResponse, type)
                apiResponse.items
            }
            is JsonArray -> {
                val subType = TypeToken.getParameterized(ApiVkResponse.ApiResponse::class.java, parameterType).type
                val type = TypeToken.getParameterized(List::class.java, subType).type
                val apiResponses: List<ApiVkResponse.ApiResponse<T>> = Gson().fromJson(rawResponse, type)
                return apiResponses.flatMap { it.items }
            }
            else -> listOf()
        }
    }

}