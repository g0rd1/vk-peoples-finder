package ru.g0rd1.peoplesfinder.common

import android.graphics.drawable.Drawable
import androidx.annotation.*

interface ResourceManager {
    fun getString(@StringRes id: Int): String
    fun getQuantityString(@PluralsRes id: Int, count: Int): String
    fun getStringArray(@ArrayRes id: Int): Array<String>
    fun getString(@StringRes id: Int, vararg arguments: String): String
    fun getDrawable(@DrawableRes id: Int): Drawable?
    fun getColor(@ColorRes id: Int): Int
    fun resolveAttribute(@AttrRes id: Int): Int
    fun getInteger(@IntegerRes id: Int): Int
}
