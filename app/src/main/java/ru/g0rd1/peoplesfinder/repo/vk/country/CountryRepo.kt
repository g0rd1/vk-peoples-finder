package ru.g0rd1.peoplesfinder.repo.vk.country

import io.reactivex.Single
import ru.g0rd1.peoplesfinder.model.Country
import ru.g0rd1.peoplesfinder.model.VkResult

interface CountryRepo {
    fun getCountries(query: String? = null): Single<VkResult<List<Country>>>
}