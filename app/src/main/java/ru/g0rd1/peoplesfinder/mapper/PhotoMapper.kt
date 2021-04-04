package ru.g0rd1.peoplesfinder.mapper

import ru.g0rd1.peoplesfinder.apiservice.model.ApiPhoto
import ru.g0rd1.peoplesfinder.model.Photo
import javax.inject.Inject

class PhotoMapper @Inject constructor() {

    fun transform(apiPhoto: ApiPhoto): Photo {
        return Photo(
            id = apiPhoto.id,
            albumId = apiPhoto.albumId,
            ownerId = apiPhoto.ownerId,
            userId = apiPhoto.userId,
            text = apiPhoto.text,
            date = apiPhoto.date,
            sizes = apiPhoto.sizes.map { transform(it) }
        )
    }

    private fun transform(apiSize: ApiPhoto.ApiSize): Photo.Size {
        return Photo.Size(
            type = apiSize.type,
            url = apiSize.url,
            width = apiSize.width,
            height = apiSize.height
        )
    }

}