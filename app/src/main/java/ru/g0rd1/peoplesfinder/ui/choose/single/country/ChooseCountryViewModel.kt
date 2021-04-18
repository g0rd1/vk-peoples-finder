package ru.g0rd1.peoplesfinder.ui.choose.single.country

import dagger.hilt.android.lifecycle.HiltViewModel
import ru.g0rd1.peoplesfinder.R
import ru.g0rd1.peoplesfinder.base.global.SingleLiveEvent
import ru.g0rd1.peoplesfinder.common.ResourceManager
import ru.g0rd1.peoplesfinder.model.Country
import ru.g0rd1.peoplesfinder.model.FilterParameters
import ru.g0rd1.peoplesfinder.model.VkResult
import ru.g0rd1.peoplesfinder.repo.filters.FiltersRepo
import ru.g0rd1.peoplesfinder.repo.vk.country.CountryRepo
import ru.g0rd1.peoplesfinder.ui.choose.single.SingleChooseItemViewData
import ru.g0rd1.peoplesfinder.ui.choose.single.SingleChooseViewModel
import ru.g0rd1.peoplesfinder.util.exhaustive
import ru.g0rd1.peoplesfinder.util.observeOnUI
import ru.g0rd1.peoplesfinder.util.subscribeOnIo
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ChooseCountryViewModel @Inject constructor(
    private val filtersRepo: FiltersRepo,
    private val countryRepo: CountryRepo,
    resourceManager: ResourceManager,
) : SingleChooseViewModel<Country>() {

    override val title: String = resourceManager.getString(R.string.choose_county_dialog_title)
    override val searchTextHint: String =
        resourceManager.getString(R.string.choose_county_dialog_search_text_hint)

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
                                    SingleChooseItemViewData(
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

    override fun onItemClick(item: SingleChooseItemViewData<Country>) {
        filtersRepo.setCountry(FilterParameters.Country.Specific(item.data))
        closeEvent.call()
    }

    override fun cancelChoice() {
        filtersRepo.setCity(FilterParameters.City.Any)
        filtersRepo.setCountry(FilterParameters.Country.Any)
        closeEvent.call()
    }

    override fun close() {
        closeEvent.call()
    }

}