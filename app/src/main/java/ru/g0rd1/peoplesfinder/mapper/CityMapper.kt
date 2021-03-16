package ru.g0rd1.peoplesfinder.mapper

import ru.g0rd1.peoplesfinder.apiservice.model.ApiCity
import ru.g0rd1.peoplesfinder.model.City
import javax.inject.Inject

class CityMapper @Inject constructor() {

    fun transform(apiCity: ApiCity): City {
        return City(
            id = apiCity.id,
            title = apiCity.title
        )
    }

}