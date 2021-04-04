package ru.g0rd1.peoplesfinder.repo.vk.photo

import io.reactivex.Single
import ru.g0rd1.peoplesfinder.model.Photo
import ru.g0rd1.peoplesfinder.model.VkResult

interface PhotoRepo {

    fun getProfilePhotos(ownerId: Int): Single<VkResult<List<Photo>>>

}