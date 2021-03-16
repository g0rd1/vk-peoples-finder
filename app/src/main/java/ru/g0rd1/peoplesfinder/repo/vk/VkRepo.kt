package ru.g0rd1.peoplesfinder.repo.vk

import io.reactivex.Single
import ru.g0rd1.peoplesfinder.apiservice.model.ApiCity
import ru.g0rd1.peoplesfinder.apiservice.model.ApiCountry
import ru.g0rd1.peoplesfinder.apiservice.model.ApiGroup
import ru.g0rd1.peoplesfinder.apiservice.model.ApiUser
import ru.g0rd1.peoplesfinder.apiservice.response.ApiVkResult

interface VkRepo {

    fun getGroups(
        userId: Int,
        offset: Int = 0,
        count: Int,
    ): Single<ApiVkResult<List<ApiGroup>>>

    fun getGroupMembers(
        code: String,
    ): Single<ApiVkResult<List<ApiUser>>>

    fun getCountries(): Single<ApiVkResult<List<ApiCountry>>>

    fun getCities(
        count: Int,
        countryId: Int? = null,
        query: String? = null,
    ): Single<ApiVkResult<List<ApiCity>>>

}