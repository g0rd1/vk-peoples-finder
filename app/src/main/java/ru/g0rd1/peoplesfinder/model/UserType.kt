package ru.g0rd1.peoplesfinder.model

import androidx.annotation.DrawableRes
import ru.g0rd1.peoplesfinder.R

data class UserType(
    val id: Int,
    val name: String,
) {

    @DrawableRes
    fun getIconRes(): Int? {
        return baseUserTypesToIconRes.keys
            .firstOrNull { it.id == this.id }?.let { baseUserTypesToIconRes[it] }
    }

    companion object {
        private val baseUserTypesToIconRes = mapOf(
            UserType(1, "Просмотренные") to R.drawable.ic_visibility,
            UserType(2, "Избранные") to R.drawable.ic_star,
            UserType(3, "Заблокированные") to R.drawable.ic_block
        )
        val VIEWED = baseUserTypesToIconRes.keys.first { it.id == 1 }
        val FAVORITE = baseUserTypesToIconRes.keys.first { it.id == 2 }
        val BLOCKED = baseUserTypesToIconRes.keys.first { it.id == 3 }
    }
}
