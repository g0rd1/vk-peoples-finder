package ru.g0rd1.peoplesfinder.util

import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import ru.g0rd1.peoplesfinder.base.BaseApplication

object ResourcesUtil {

    object Strings {
        fun get(@StringRes stringRes: Int, vararg formatArgs: Any = emptyArray()): String {
            return BaseApplication.instance.getString(stringRes, *formatArgs)
        }
    }

    object Floats {
        fun get(floatRes: Int): Float {
            val outValue = TypedValue()
            BaseApplication.instance.resources.getValue(floatRes, outValue, true)
            return outValue.float
        }
    }

    object Drawables {
        fun get(@DrawableRes drawableRes: Int): Drawable? {
            return ContextCompat.getDrawable(BaseApplication.instance, drawableRes)
        }
    }
}