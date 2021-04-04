package ru.g0rd1.peoplesfinder.ui.photosViewing

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ru.g0rd1.peoplesfinder.R
import ru.g0rd1.peoplesfinder.base.BaseViewModel
import ru.g0rd1.peoplesfinder.base.global.SingleLiveEvent
import ru.g0rd1.peoplesfinder.common.ResourceManager
import ru.g0rd1.peoplesfinder.model.Photo
import ru.g0rd1.peoplesfinder.model.VkResult
import ru.g0rd1.peoplesfinder.repo.vk.photo.PhotoRepo
import ru.g0rd1.peoplesfinder.util.exhaustive
import ru.g0rd1.peoplesfinder.util.observeOnUI
import ru.g0rd1.peoplesfinder.util.subscribeOnIo


class PhotosViewingViewModel @AssistedInject constructor(
    @Assisted private val userId: Int,
    private val photoRepo: PhotoRepo,
    private val resManager: ResourceManager,
) : BaseViewModel() {

    val errorText = ObservableField<String>()
    val errorVisible = ObservableBoolean()
    val loaderVisible = ObservableBoolean()
    val sequentialNumber = ObservableInt()
    val count = ObservableInt()

    val getPhotosEvent = SingleLiveEvent<List<String>>()
    val closeEvent = SingleLiveEvent<Unit>()

    val positionChanged: (position: Int) -> Unit = { position ->
        sequentialNumber.set(position + 1)
    }

    override fun onStart() {
        loadPhotos()
    }

    private fun loadPhotos() {
        loaderVisible.set(true)
        errorVisible.set(false)
        photoRepo.getProfilePhotos(userId)
            .subscribeOnIo()
            .observeOnUI()
            .subscribe(
                {
                    handlePhotosVkResult(it)
                },
                {
                    handleGenericError()
                }
            ).disposeLater()
    }

    private fun handlePhotosVkResult(photosVkResult: VkResult<List<Photo>>) {
        when (photosVkResult) {
            is VkResult.Error.ApiVk -> handleError(photosVkResult.error.message)
            is VkResult.Error.Generic -> handleGenericError()
            is VkResult.Success -> {
                val urls = photosVkResult.data.mapNotNull { photo ->
                    photo.sizes.maxByOrNull { it.width * it.height }?.url
                }
                if (urls.isEmpty()) {
                    handleError(resManager.getString(R.string.fragment_photos_viewing_no_photos_error))
                } else {
                    getPhotosEvent.value = urls
                    count.set(urls.size)
                    loaderVisible.set(false)
                    errorVisible.set(false)
                }
            }
        }.exhaustive
    }

    private fun handleGenericError() {
        handleError(resManager.getString(R.string.unknown_error))
    }

    private fun handleError(errorText: String) {
        this.errorText.set(errorText)
        errorVisible.set(true)
        loaderVisible.set(false)
    }

    fun close() {
        closeEvent.call()
    }

    @AssistedFactory
    interface Factory {
        fun create(userId: Int): PhotosViewingViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            userId: Int,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return assistedFactory.create(userId) as T
            }
        }
    }

}
