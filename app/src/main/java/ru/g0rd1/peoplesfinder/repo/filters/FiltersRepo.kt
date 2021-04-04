package ru.g0rd1.peoplesfinder.repo.filters

import io.reactivex.Observable
import ru.g0rd1.peoplesfinder.model.FilterParameters

interface FiltersRepo {

    fun setAgeFrom(ageFrom: FilterParameters.Age)

    fun setAgeTo(ageTo: FilterParameters.Age)

    fun setSex(sex: FilterParameters.Sex)

    fun setRelation(relation: FilterParameters.Relation)

    fun setCountry(country: FilterParameters.Country)

    fun setCity(city: FilterParameters.City)

    fun setHasPhoto(hasPhoto: Boolean)

    fun setNotClosed(notClosed: Boolean)

    fun setRequiredGroupIds(requiredGroupIds: List<Int>)

    fun getFilterParameters(): FilterParameters

    fun observerFilterParameters(): Observable<FilterParameters>

}