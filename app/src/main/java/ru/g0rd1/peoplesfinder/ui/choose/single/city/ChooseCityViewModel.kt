package ru.g0rd1.peoplesfinder.ui.choose.single.city

import ru.g0rd1.peoplesfinder.base.global.SingleLiveEvent
import ru.g0rd1.peoplesfinder.model.City
import ru.g0rd1.peoplesfinder.model.FilterParameters
import ru.g0rd1.peoplesfinder.model.VkResult
import ru.g0rd1.peoplesfinder.repo.filters.FiltersRepo
import ru.g0rd1.peoplesfinder.repo.vk.city.CityRepo
import ru.g0rd1.peoplesfinder.ui.choose.single.SingleChooseItemViewModel
import ru.g0rd1.peoplesfinder.ui.choose.single.SingleChooseViewModel
import ru.g0rd1.peoplesfinder.util.exhaustive
import ru.g0rd1.peoplesfinder.util.observeOnUI
import ru.g0rd1.peoplesfinder.util.subscribeOnIo
import timber.log.Timber
import javax.inject.Inject

class ChooseCityViewModel @Inject constructor(
    private val filtersRepo: FiltersRepo,
    private val cityRepo: CityRepo
) : SingleChooseViewModel<City>() {

    val closeEvent = SingleLiveEvent<Unit>()

    override val cancelChoiceVisible: Boolean =
        (filtersRepo.getFilterParameters().city is FilterParameters.City.Specific)

    override fun onStart() {
        super.onStart()
        observe()
    }

    private fun observe() {
        observeSearchText()
            .doOnEach { loaderVisible.set(true) }
            .flatMapSingle {
                val countryId = when (val country = filtersRepo.getFilterParameters().country) {
                    FilterParameters.Country.Any -> null
                    is FilterParameters.Country.Specific -> country.data.id
                }
                cityRepo.getCities(countryId, it)
            }
            .subscribeOnIo()
            .observeOnUI()
            .subscribe(
                {
                    when (it) {
                        is VkResult.Error.ApiVk -> {
                            loaderVisible.set(false)
                            setError(it.error.message)
                            items.set(listOf())
                        }
                        is VkResult.Error.Generic -> {
                            loaderVisible.set(false)
                            // TODO заменить на строковый ресурс
                            setError("Не удалось получить список городов.\nПопробуйте выбрать город позже.")
                            items.set(listOf())
                        }
                        is VkResult.Success -> {
                            loaderVisible.set(false)
                            clearError()
                            items.set(
                                it.data.map { city ->
                                    SingleChooseItemViewModel(
                                        data = city,
                                        name = city.title,
                                        id = city.id
                                    )
                                }
                            )
                        }
                    }.exhaustive
                },
                Timber::e
            ).disposeLater()
    }

    override fun onItemClick(position: Int) {
        items.get()?.get(position)?.let { filtersRepo.setCity(FilterParameters.City.Specific(it.data as City)) }
        closeEvent.call()
    }

    override fun cancelChoice() {
        filtersRepo.setCity(FilterParameters.City.Any)
        closeEvent.call()
    }

}