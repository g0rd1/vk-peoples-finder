package ru.g0rd1.peoplesfinder.ui.choose.single.country

import ru.g0rd1.peoplesfinder.base.global.SingleLiveEvent
import ru.g0rd1.peoplesfinder.model.Country
import ru.g0rd1.peoplesfinder.model.FilterParameters
import ru.g0rd1.peoplesfinder.model.VkResult
import ru.g0rd1.peoplesfinder.repo.filters.FiltersRepo
import ru.g0rd1.peoplesfinder.repo.vk.country.CountryRepo
import ru.g0rd1.peoplesfinder.ui.choose.single.SingleChooseItemViewModel
import ru.g0rd1.peoplesfinder.ui.choose.single.SingleChooseViewModel
import ru.g0rd1.peoplesfinder.util.exhaustive
import ru.g0rd1.peoplesfinder.util.observeOnUI
import ru.g0rd1.peoplesfinder.util.subscribeOnIo
import timber.log.Timber
import javax.inject.Inject

class ChooseCountryViewModel @Inject constructor(
    private val filtersRepo: FiltersRepo,
    private val countryRepo: CountryRepo
) : SingleChooseViewModel<Country>() {

    val closeEvent = SingleLiveEvent<Unit>()

    override val cancelChoiceVisible: Boolean =
        (filtersRepo.getFilterParameters().country is FilterParameters.Country.Specific)

    override fun onStart() {
        super.onStart()
        observe()
    }

    private fun observe() {
        observeSearchText()
            .doOnEach { loaderVisible.set(true) }
            .flatMapSingle { countryRepo.getCountries(it) }
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
                                it.data.map { country ->
                                    SingleChooseItemViewModel(
                                        data = country,
                                        name = country.title,
                                        id = country.id
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
        items.get()?.get(position)
            ?.let { filtersRepo.setCountry(FilterParameters.Country.Specific(it.data as Country)) }
        closeEvent.call()
    }

    override fun cancelChoice() {
        filtersRepo.setCountry(FilterParameters.Country.Any)
        closeEvent.call()
    }

}