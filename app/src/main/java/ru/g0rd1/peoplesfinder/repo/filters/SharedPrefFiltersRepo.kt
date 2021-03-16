package ru.g0rd1.peoplesfinder.repo.filters

import android.content.Context
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import ru.g0rd1.peoplesfinder.model.FilterParameters
import ru.g0rd1.peoplesfinder.repo.SimpleSharedPrefRepo
import javax.inject.Inject

class SharedPrefFiltersRepo @Inject constructor(
    context: Context
) : SimpleSharedPrefRepo(SHARED_PREF_NAME, context), FiltersRepo {

    private val filterParametersSubject: Subject<FilterParameters> = BehaviorSubject.createDefault(getFilterParameters())

    override fun setAgeFrom(ageFrom: FilterParameters.Age) {
        putObject(AGE_FROM_KEY, ageFrom)
        filterParametersSubject.onNext(getFilterParameters())
    }

    override fun setAgeTo(ageTo: FilterParameters.Age) {
        putObject(AGE_TO_KEY, ageTo)
        filterParametersSubject.onNext(getFilterParameters())
    }

    override fun setRelation(relation: FilterParameters.Relation) {
        putObject(RELATION_KEY, relation)
        filterParametersSubject.onNext(getFilterParameters())
    }

    override fun setCountry(country: FilterParameters.Country) {
        putObject(COUNTRY_KEY, country)
        filterParametersSubject.onNext(getFilterParameters())
    }

    override fun setCity(city: FilterParameters.City) {
        putObject(CITY_KEY, city)
        filterParametersSubject.onNext(getFilterParameters())
    }

    override fun setHasPhoto(hasPhoto: Boolean) {
        putBoolean(HAS_PHOTO_KEY, hasPhoto)
        filterParametersSubject.onNext(getFilterParameters())
    }

    override fun setRequiredGroupIds(requiredGroupIds: List<Int>) {
        putList(REQUIRED_GROUPS_KEY, requiredGroupIds)
        filterParametersSubject.onNext(getFilterParameters())
    }

    override fun getFilterParameters(): FilterParameters {
        return FilterParameters(
            ageFrom = getObject(AGE_FROM_KEY, FilterParameters.Age.Any),
            ageTo = getObject(AGE_TO_KEY, FilterParameters.Age.Any),
            sex = getObject(SEX_KEY, FilterParameters.Sex.ANY),
            relation = getObject(RELATION_KEY, FilterParameters.Relation.ANY),
            country = getObject(COUNTRY_KEY, FilterParameters.Country.Any),
            city = getObject(CITY_KEY, FilterParameters.City.Any),
            hasPhoto = getBoolean(HAS_PHOTO_KEY, false),
            requiredGroupIds = getList(REQUIRED_GROUPS_KEY)
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
        private const val REQUIRED_GROUPS_KEY = "REQUIRED_GROUPS_KEY"
    }

}