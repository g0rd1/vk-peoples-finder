package ru.g0rd1.peoplesfinder.mapper

import ru.g0rd1.peoplesfinder.apiservice.model.ApiCountry
import ru.g0rd1.peoplesfinder.model.Country
import javax.inject.Inject

class CountryMapper @Inject constructor() {

    fun transform(apiCountry: ApiCountry): Country {
        return Country(
            id = apiCountry.id,
            title = apiCountry.title
        )
    }

}