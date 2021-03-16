package ru.g0rd1.peoplesfinder.repo.vk.city

import io.reactivex.Single
import ru.g0rd1.peoplesfinder.model.City
import ru.g0rd1.peoplesfinder.model.VkResult

interface CityRepo {
    fun getCities(countryId: Int? = null, query: String? = null): Single<VkResult<List<City>>>
}