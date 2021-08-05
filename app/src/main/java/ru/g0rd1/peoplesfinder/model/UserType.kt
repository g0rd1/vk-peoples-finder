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
        const val FAVORITE_ID = 1
        const val BLOCKED_ID = 2
        val FAVORITE = UserType(FAVORITE_ID, "Избранные")
        val BLOCKED = UserType(BLOCKED_ID, "Заблокированные")
        private val baseUserTypesToIconRes = mapOf(
            FAVORITE to R.drawable.ic_star,
            BLOCKED to R.drawable.ic_block
        )
    }
}
