package ru.g0rd1.peoplesfinder.ui.settings

import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import ru.g0rd1.peoplesfinder.model.FilterParameters
import ru.g0rd1.peoplesfinder.repo.filters.FiltersRepo
import ru.g0rd1.peoplesfinder.util.observeOnUI
import ru.g0rd1.peoplesfinder.util.subscribeOnIo
import timber.log.Timber
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val filtersRepo: FiltersRepo,
) : ViewModel() {

    private val disposables = CompositeDisposable()

    val selectedAgeFromPosition = ObservableInt()
    val selectedAgeToPosition = ObservableInt()
    val selectedSexPosition = ObservableInt()
    val selectedRelationPosition = ObservableInt()

    val country = ObservableField<String>()
    val city = ObservableField<String>()
    val hasPhoto = ObservableBoolean()
    val requiredGroupIdsCount = ObservableInt(0)

    val cityVisible = ObservableBoolean()

    val ageFromItems: List<FilterParameters.Age> = listOf(FilterParameters.Age.Any)
        .plus((MIN_AGE..MAX_AGE).map { FilterParameters.Age.Specific(it) })

    val ageToItems: List<FilterParameters.Age> = listOf(FilterParameters.Age.Any)
        .plus((MIN_AGE..MAX_AGE).map { FilterParameters.Age.Specific(it) })

    val sexItems: List<FilterParameters.Sex> = FilterParameters.Sex.values().toList()

    val relationItems: List<FilterParameters.Relation> = FilterParameters.Relation.values().toList()

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

    fun onStart() {
        observeFilters()
        selectedAgeFromPosition.addOnPropertyChangedCallback(selectedAgeFromPositionListener)
        selectedAgeToPosition.addOnPropertyChangedCallback(selectedAgeToPositionListener)
    }

    fun hasPhotoClicked() {
        filtersRepo.setHasPhoto(!hasPhoto.get())
    }

    private fun observeFilters() {
        filtersRepo.observerFilterParameters()
            .subscribeOnIo()
            .observeOnUI()
            .subscribe(
                { filterParameters ->
                    selectedAgeFromPosition.set(ageFromItems.indexOfFirst { filterParameters.ageFrom == it })
                    selectedAgeToPosition.set(ageToItems.indexOfFirst { filterParameters.ageTo == it })
                    selectedSexPosition.set(sexItems.indexOfFirst { filterParameters.sex == it })
                    selectedRelationPosition.set(relationItems.indexOfFirst { filterParameters.relation == it })
                    country.set(filterParameters.country.name)
                    city.set(filterParameters.city.name)
                    hasPhoto.set(filterParameters.hasPhoto)
                    requiredGroupIdsCount.set(filterParameters.requiredGroupIds.size)
                },
                Timber::e
            ).addTo(disposables)
    }

    override fun onCleared() {
        super.onCleared()
        selectedAgeFromPosition.removeOnPropertyChangedCallback(selectedAgeFromPositionListener)
        selectedAgeToPosition.removeOnPropertyChangedCallback(selectedAgeToPositionListener)

        disposables.clear()
    }

    companion object {
        private const val MIN_AGE = 14
        private const val MAX_AGE = 99
    }

}