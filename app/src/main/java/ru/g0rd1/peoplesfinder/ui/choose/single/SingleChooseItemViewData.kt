package ru.g0rd1.peoplesfinder.ui.choose.single

data class SingleChooseItemViewData<T>(
    val data: T,
    val name: String,
    val id: Int,
    val imageUrl: String? = null
)