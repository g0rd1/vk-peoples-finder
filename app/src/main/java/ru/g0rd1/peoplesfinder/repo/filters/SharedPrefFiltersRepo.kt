package ru.g0rd1.peoplesfinder.repo.filters

import android.content.Context
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.g0rd1.peoplesfinder.model.FilterParameters
import ru.g0rd1.peoplesfinder.repo.SimpleSharedPrefRepo
import javax.inject.Inject

class SharedPrefFiltersRepo @Inject constructor(
    context: Context,
) : SimpleSharedPrefRepo(SHARED_PREF_NAME, context), FiltersRepo {

    private val filterParametersSubject: Subject<FilterParameters> =
        BehaviorSubject.createDefault(getFilterParameters())

    override fun setAgeFrom(ageFrom: FilterParameters.Age) {
        putString(AGE_FROM_KEY, Json.encodeToString(ageFrom))
        filterParametersSubject.onNext(getFilterParameters())
    }

    override fun setAgeTo(ageTo: FilterParameters.Age) {
        putString(AGE_TO_KEY, Json.encodeToString(ageTo))
        filterParametersSubject.onNext(getFilterParameters())
    }

    override fun setSex(sex: FilterParameters.Sex) {
        putString(SEX_KEY, Json.encodeToString(sex))
    }

    override fun setRelation(relation: FilterParameters.Relation) {
        putString(RELATION_KEY, Json.encodeToString(relation))
        filterParametersSubject.onNext(getFilterParameters())
    }

    override fun setCountry(country: FilterParameters.Country) {
        putString(COUNTRY_KEY, Json.encodeToString(country))
        filterParametersSubject.onNext(getFilterParameters())
    }

    override fun setCity(city: FilterParameters.City) {
        putString(CITY_KEY, Json.encodeToString(city))
        filterParametersSubject.onNext(getFilterParameters())
    }

    override fun setHasPhoto(hasPhoto: Boolean) {
        putBoolean(HAS_PHOTO_KEY, hasPhoto)
        filterParametersSubject.onNext(getFilterParameters())
    }

    override fun setNotClosed(notClosed: Boolean) {
        putBoolean(NOT_CLOSED_KEY, notClosed)
        filterParametersSubject.onNext(getFilterParameters())
    }

    override fun setRequiredGroupIds(requiredGroupIds: List<Int>) {
        putList(REQUIRED_GROUPS_KEY, requiredGroupIds, Int::class.java)
        filterParametersSubject.onNext(getFilterParameters())
    }

    override fun getFilterParameters(): FilterParameters {
        return FilterParameters(
            ageFrom = Json.decodeFromString(
                getString(
                    AGE_FROM_KEY,
                    Json.encodeToString<FilterParameters.Age>(FilterParameters.Age.Any)
                )
            ),
            ageTo = Json.decodeFromString(
                getString(
                    AGE_TO_KEY,
                    Json.encodeToString<FilterParameters.Age>(FilterParameters.Age.Any)
                )
            ),
            sex = Json.decodeFromString(
                getString(
                    SEX_KEY,
                    Json.encodeToString<FilterParameters.Sex>(FilterParameters.Sex.Any)
                )
            ),
            relation = Json.decodeFromString(
                getString(
                    RELATION_KEY,
                    Json.encodeToString<FilterParameters.Relation>(FilterParameters.Relation.Any)
                )
            ),
            country = Json.decodeFromString(
                getString(
                    COUNTRY_KEY,
                    Json.encodeToString<FilterParameters.Country>(FilterParameters.Country.Any)
                )
            ),
            city = Json.decodeFromString(
                getString(
                    CITY_KEY,
                    Json.encodeToString<FilterParameters.City>(FilterParameters.City.Any)
                )
            ),
            hasPhoto = getBoolean(HAS_PHOTO_KEY, false),
            notClosed = getBoolean(NOT_CLOSED_KEY, false),
            requiredGroupIds = getList(REQUIRED_GROUPS_KEY, listOf(), Int::class.java)
        )
    }

    override fun observerFilterParameters(): Observable<FilterParameters> {
        return filterParametersSubject.distinctUntilChanged()
    }

    companion object {
        private const val SHARED_PREF_NAME = "FiltersRepo"
        private const val AGE_FROM_KEY = "AGE_FROM_KEY"
        private const val AGE_TO_KEY = "AGE_TO_KEY"
        private const val SEX_KEY = "SEX_KEY"
        private const val RELATION_KEY = "RELATION_KEY"
        private const val COUNTRY_KEY = "COUNTRY_KEY"
        private const val CITY_KEY = "CITY_KEY"
        private const val HAS_PHOTO_KEY = "HAS_PHOTO_KEY"
        private const val NOT_CLOSED_KEY = "NOT_CLOSED_KEY"
        private const val REQUIRED_GROUPS_KEY = "REQUIRED_GROUPS_KEY"
    }

}