package ru.g0rd1.peoplesfinder.ui.choose.multi

data class MultichooseItemViewData<T>(
    val data: T,
    val name: String,
    val id: Int,
    val choosed: Boolean,
    val imageUrl: String? = null
)