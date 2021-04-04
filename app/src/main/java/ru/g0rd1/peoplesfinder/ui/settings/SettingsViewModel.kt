package ru.g0rd1.peoplesfinder.ui.settings

import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.g0rd1.peoplesfinder.R
import ru.g0rd1.peoplesfinder.base.BaseViewModel
import ru.g0rd1.peoplesfinder.base.global.SingleLiveEvent
import ru.g0rd1.peoplesfinder.common.ResourceManager
import ru.g0rd1.peoplesfinder.common.enums.Relation
import ru.g0rd1.peoplesfinder.common.enums.Sex
import ru.g0rd1.peoplesfinder.model.FilterParameters
import ru.g0rd1.peoplesfinder.repo.filters.FiltersRepo
import ru.g0rd1.peoplesfinder.util.observeOnUI
import ru.g0rd1.peoplesfinder.util.subscribeOnIo
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val filtersRepo: FiltersRepo,
    private val resourceManager: ResourceManager,
) : BaseViewModel() {

    val selectedAgeFromPosition = ObservableInt()
    val selectedAgeToPosition = ObservableInt()
    val selectedSexPosition = ObservableInt()
    val selectedRelationPosition = ObservableInt()

    val country = ObservableField<String>()
    val city = ObservableField<String>()
    val hasPhoto = ObservableBoolean()
    val notClosed = ObservableBoolean()
    val requiredGroupIdsCount = ObservableInt(0)

    val cityVisible = ObservableBoolean()

    val showChooseCityDialog = SingleLiveEvent<Unit>()
    val showChooseCountryDialog = SingleLiveEvent<Unit>()
    val showChooseRequiredGroupsDialog = SingleLiveEvent<Unit>()

    lateinit var ageFromItems: List<FilterParameters.Age>

    val ageFromTextItems = ObservableField<List<String>>()

    lateinit var ageToItems: List<FilterParameters.Age>

    val ageToTextItems = ObservableField<List<String>>()

    val sexItems: List<FilterParameters.Sex> = listOf(FilterParameters.Sex.Any)
        .plus(
            Sex.values()
                .filterNot { it == Sex.NOT_SPECIFIED }
                .map { FilterParameters.Sex.Specific(it) }
        )

    val sexTextItems: List<String>
        get() = sexItems.map {
            when (it) {
                FilterParameters.Sex.Any -> resourceManager.getString(R.string.any_masculine)
                is FilterParameters.Sex.Specific -> resourceManager.getString(it.sex.getStringResource())
            }
        }

    val relationItems: List<FilterParameters.Relation> = listOf(FilterParameters.Relation.Any)
        .plus(
            Relation.values()
                .filterNot { it == Relation.NOT_SPECIFIED }
                .map { FilterParameters.Relation.Specific(it) }
        )

    val relationTextItems: List<String>
        get() = relationItems.map { relationFilterParameter ->
            when (relationFilterParameter) {
                FilterParameters.Relation.Any -> resourceManager.getString(R.string.any_neuter)
                is FilterParameters.Relation.Specific -> resourceManager.getString(
                    when (val sex = sexItems[selectedSexPosition.get()]) {
                        FilterParameters.Sex.Any -> relationFilterParameter.relation.getStringResource()
                        is FilterParameters.Sex.Specific -> relationFilterParameter.relation.getStringResource(sex.sex)
                    }
                )
            }
        }

    private val selectedAgeFromPositionListener = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            filtersRepo.setAgeFrom(ageFromItems[selectedAgeFromPosition.get()])
        }
    }

    private val selectedAgeToPositionListener = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            filtersRepo.setAgeTo(ageToItems[selectedAgeToPosition.get()])
        }
    }

    private val selectedSexPositionListener = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            filtersRepo.setSex(sexItems[selectedSexPosition.get()])
        }
    }

    private val selectRelationPositionListener = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            filtersRepo.setRelation(relationItems[selectedRelationPosition.get()])
        }
    }

    override fun onStart() {
        observeFilters()
        selectedAgeFromPosition.addOnPropertyChangedCallback(selectedAgeFromPositionListener)
        selectedAgeToPosition.addOnPropertyChangedCallback(selectedAgeToPositionListener)
        selectedSexPosition.addOnPropertyChangedCallback(selectedSexPositionListener)
        selectedRelationPosition.addOnPropertyChangedCallback(selectRelationPositionListener)
    }

    fun hasPhotoClicked() {
        filtersRepo.setHasPhoto(!hasPhoto.get())
    }

    fun notClosedClicked() {
        filtersRepo.setNotClosed(!notClosed.get())
    }

    fun chooseCountry() {
        showChooseCountryDialog.call()
    }

    fun chooseCity() {
        showChooseCityDialog.call()
    }

    fun chooseRequiredGroups() {
        showChooseRequiredGroupsDialog.call()
    }

    private fun observeFilters() {
        filtersRepo.observerFilterParameters()
            .subscribeOnIo()
            .observeOnUI()
            .subscribe(
                { filterParameters ->
                    val minAgeForAgeTo = when (filterParameters.ageFrom) {
                        FilterParameters.Age.Any -> MIN_AGE
                        is FilterParameters.Age.Specific -> filterParameters.ageFrom.age
                    }
                    val maxAgeForAgeFrom = when (filterParameters.ageTo) {
                        FilterParameters.Age.Any -> MAX_AGE
                        is FilterParameters.Age.Specific -> filterParameters.ageTo.age
                    }
                    ageFromItems = listOf(FilterParameters.Age.Any)
                        .plus((MIN_AGE..maxAgeForAgeFrom).map { FilterParameters.Age.Specific(it) })
                    ageToItems = listOf(FilterParameters.Age.Any)
                        .plus((minAgeForAgeTo..MAX_AGE).map { FilterParameters.Age.Specific(it) })
                    ageFromTextItems.set(
                        ageFromItems.map {
                            when (it) {
                                FilterParameters.Age.Any -> "любой"
                                is FilterParameters.Age.Specific -> it.age.toString()
                            }
                        }
                    )
                    ageToTextItems.set(
                        ageToItems.map {
                            when (it) {
                                FilterParameters.Age.Any -> "любой"
                                is FilterParameters.Age.Specific -> it.age.toString()
                            }
                        }
                    )
                    selectedAgeFromPosition.set(ageFromItems.indexOfFirst { filterParameters.ageFrom == it })
                    selectedAgeToPosition.set(ageToItems.indexOfFirst { filterParameters.ageTo == it })
                    selectedSexPosition.set(sexItems.indexOfFirst { filterParameters.sex == it })
                    selectedRelationPosition.set(relationItems.indexOfFirst { filterParameters.relation == it })
                    country.set(
                        when (filterParameters.country) {
                            FilterParameters.Country.Any -> resourceManager.getString(R.string.any_feminine)
                            is FilterParameters.Country.Specific -> filterParameters.country.country.title
                        }
                    )
                    city.set(
                        when (filterParameters.city) {
                            FilterParameters.City.Any -> resourceManager.getString(R.string.any_masculine)
                            is FilterParameters.City.Specific -> filterParameters.city.city.title
                        }
                    )
                    hasPhoto.set(filterParameters.hasPhoto)
                    notClosed.set(filterParameters.notClosed)
                    requiredGroupIdsCount.set(filterParameters.requiredGroupIds.size)
                    cityVisible.set(filterParameters.country is FilterParameters.Country.Specific)
                },
                Timber::e
            ).disposeLater()
    }

    override fun onCleared() {
        super.onCleared()
        selectedAgeFromPosition.removeOnPropertyChangedCallback(selectedAgeFromPositionListener)
        selectedAgeToPosition.removeOnPropertyChangedCallback(selectedAgeToPositionListener)
        selectedSexPosition.removeOnPropertyChangedCallback(selectedSexPositionListener)
        selectedRelationPosition.removeOnPropertyChangedCallback(selectRelationPositionListener)
    }

    companion object {
        private const val MIN_AGE = 14
        private const val MAX_AGE = 99
    }

}