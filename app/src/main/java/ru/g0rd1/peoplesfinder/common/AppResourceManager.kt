package ru.g0rd1.peoplesfinder.common

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.*
import androidx.core.content.ContextCompat
import javax.inject.Inject

class AppResourceManager @Inject constructor(private val context: Context) : ResourceManager {

    override fun getString(@StringRes id: Int): String {
        return context.getString(id)
    }

    override fun getStringArray(@ArrayRes id: Int): Array<String> {
        return context.resources.getStringArray(id)
    }

    override fun getString(@StringRes id: Int, vararg arguments: String): String {
        return context.getString(id, *arguments)
    }

    override fun getQuantityString(@PluralsRes id: Int, count: Int): String {
        return context.resources.getQuantityString(id, count, count)
    }

    override fun getDrawable(@DrawableRes id: Int): Drawable? {
        return ContextCompat.getDrawable(context, id)
    }

    override fun getColor(@ColorRes id: Int): Int {
        return ContextCompat.getColor(context, id)
    }

    override fun resolveAttribute(@AttrRes id: Int): Int {
        val outValue = TypedValue()
        context.theme.resolveAttribute(id, outValue, true)
        return outValue.resourceId
    }

    override fun getInteger(@IntegerRes id: Int): Int {
        return context.resources.getInteger(id)
    }
}
