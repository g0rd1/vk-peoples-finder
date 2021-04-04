package ru.g0rd1.peoplesfinder.model

import ru.g0rd1.peoplesfinder.common.enums.PhotoType

data class Photo(
    val id: Int,
    val albumId: Int,
    val ownerId: Int,
    val userId: Int,
    val text: String,
    val date: Int,
    val sizes: List<Size>,
) {
    data class Size(
        val type: PhotoType,
        val url: String,
        val width: Int,
        val height: Int,
    )
}