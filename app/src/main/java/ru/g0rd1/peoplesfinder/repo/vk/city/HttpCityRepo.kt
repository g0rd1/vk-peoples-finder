package ru.g0rd1.peoplesfinder.repo.vk.city

import io.reactivex.Single
import ru.g0rd1.peoplesfinder.mapper.CityMapper
import ru.g0rd1.peoplesfinder.mapper.VkResultMapper
import ru.g0rd1.peoplesfinder.model.City
import ru.g0rd1.peoplesfinder.model.VkResult
import ru.g0rd1.peoplesfinder.repo.vk.VkRepo
import javax.inject.Inject

class HttpCityRepo @Inject constructor(
    private val vkRepo: VkRepo,
    private val cityMapper: CityMapper,
    private val vkResultMapper: VkResultMapper
) : CityRepo {

    override fun getCities(countryId: Int?, query: String?): Single<VkResult<List<City>>> {
        return vkRepo.getCities(
            count = 1000,
            countryId = countryId,
            query = query,
        ).map { apiVkResult ->
            vkResultMapper.transform(apiVkResult) { apiCities ->
                apiCities.map { cityMapper.transform(it) }
            }
        }
    }

}