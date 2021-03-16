package ru.g0rd1.peoplesfinder.repo.filters

import io.reactivex.Observable
import ru.g0rd1.peoplesfinder.model.FilterParameters

interface FiltersRepo {

    fun setAgeFrom(ageFrom: FilterParameters.Age)

    fun setAgeTo(ageTo: FilterParameters.Age)

    fun setRelation(relation: FilterParameters.Relation)

    fun setCountry(country: FilterParameters.Country)

    fun setCity(city: FilterParameters.City)

    fun setHasPhoto(hasPhoto: Boolean)

    fun setRequiredGroupIds(requiredGroupIds: List<Int>)

    fun getFilterParameters(): FilterParameters

    fun observerFilterParameters(): Observable<FilterParameters>

}