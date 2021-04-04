package ru.g0rd1.peoplesfinder.repo.vk.country

import io.reactivex.Single
import ru.g0rd1.peoplesfinder.mapper.CountryMapper
import ru.g0rd1.peoplesfinder.mapper.VkResultMapper
import ru.g0rd1.peoplesfinder.model.Country
import ru.g0rd1.peoplesfinder.model.VkResult
import ru.g0rd1.peoplesfinder.repo.vk.VkRepo
import ru.g0rd1.peoplesfinder.util.subscribeOnIo
import javax.inject.Inject

class HttpCountryRepo @Inject constructor(
    private val vkRepo: VkRepo,
    private val countryMapper: CountryMapper,
    private val vkResultMapper: VkResultMapper
) : CountryRepo {

    @Volatile
    private var countriesCache: List<Country>? = null

    override fun getCountries(query: String?): Single<VkResult<List<Country>>> {
        val countriesCache = this.countriesCache
        if (countriesCache != null) return Single.just(
            VkResult.Success(
                getCountriesStartWithQuery(
                    countriesCache,
                    query
                )
            )
        )
        return vkRepo.getCountries().map { apiVkResult ->
            val vkResult = vkResultMapper.transform(apiVkResult) { apiCountries ->
                apiCountries.map { countryMapper.transform(it) }
            }
            if (vkResult is VkResult.Success) {
                this.countriesCache = vkResult.data
                VkResult.Success(getCountriesStartWithQuery(vkResult.data, query))
            } else {
                vkResult
            }
        }.subscribeOnIo()
    }

    private fun getCountriesStartWithQuery(
        countries: List<Country>,
        query: String?
    ): List<Country> {
        val formattedQuery = query?.trim() ?: ""
        return countries.filter { it.title.startsWith(prefix = formattedQuery, ignoreCase = true) }
    }

}