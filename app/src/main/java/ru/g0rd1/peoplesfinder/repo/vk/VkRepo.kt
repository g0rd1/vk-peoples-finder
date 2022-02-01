package ru.g0rd1.peoplesfinder.repo.vk

import io.reactivex.Single
import ru.g0rd1.peoplesfinder.apiservice.model.*
import ru.g0rd1.peoplesfinder.apiservice.response.ApiVkResult

interface VkRepo {

    fun getGroups(
        userId: Int,
        offset: Int = 0,
        count: Int,
    ): Single<ApiVkResult<List<ApiGroup>>>

    fun searchGroups(searchText: String): Single<ApiVkResult<List<ApiGroup>>>

    fun getGroupMembers(
        groupId: String,
        offset: Int,
        count: Int = 1000,
    ): Single<ApiVkResult<List<ApiUser>>>

    fun getCountries(): Single<ApiVkResult<List<ApiCountry>>>

    fun getCities(
        count: Int,
        countryId: Int? = null,
        query: String? = null,
        needAll: Boolean = !query.isNullOrBlank(),
    ): Single<ApiVkResult<List<ApiCity>>>

    fun getProfilePhotos(ownerId: Int): Single<ApiVkResult<List<ApiPhoto>>>

}