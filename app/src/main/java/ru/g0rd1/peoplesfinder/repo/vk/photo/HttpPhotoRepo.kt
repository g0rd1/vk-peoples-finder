package ru.g0rd1.peoplesfinder.repo.vk.photo

import io.reactivex.Single
import ru.g0rd1.peoplesfinder.mapper.PhotoMapper
import ru.g0rd1.peoplesfinder.mapper.VkResultMapper
import ru.g0rd1.peoplesfinder.model.Photo
import ru.g0rd1.peoplesfinder.model.VkResult
import ru.g0rd1.peoplesfinder.repo.vk.VkRepo
import javax.inject.Inject

class HttpPhotoRepo @Inject constructor(
    private val vkRepo: VkRepo,
    private val photoMapper: PhotoMapper,
    private val vkResultMapper: VkResultMapper,
) : PhotoRepo {

    override fun getProfilePhotos(ownerId: Int): Single<VkResult<List<Photo>>> {
        return vkRepo.getProfilePhotos(ownerId).map { apiVkResult ->
            vkResultMapper.transform(apiVkResult) { apiPhotos ->
                apiPhotos.map { photoMapper.transform(it) }
            }
        }
    }

}