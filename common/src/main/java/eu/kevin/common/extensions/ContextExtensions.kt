package eu.kevin.common.extensions

import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import androidx.annotation.*
import androidx.core.content.ContextCompat
import java.util.*

@ColorInt
fun Context.getColorFromAttr(@AttrRes attribute: Int): Int {
    return TypedValue().let {
        theme.resolveAttribute(attribute, it, true)
        it.data
    }
}

@StyleRes
fun Context.getStyleFromAttr(@AttrRes attribute: Int): Int {
    return TypedValue().let {
        theme.resolveAttribute(attribute, it, true)
        it.data
    }
}

@ColorInt
fun Context.getColorCompat(@ColorRes res: Int): Int {
    return ContextCompat.getColor(this, res)
}

fun Context.getDrawableCompat(@DrawableRes id: Int): Drawable? {
    return ContextCompat.getDrawable(this, id)
}

fun Context.getCurrentLocale(): Locale {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        resources.configuration.locales.get(0)
    } else {
        resources.configuration.locale
    }
}

fun Context.isDarkMode(): Boolean {
    return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
        Configuration.UI_MODE_NIGHT_YES -> true
        Configuration.UI_MODE_NIGHT_NO -> false
        Configuration.UI_MODE_NIGHT_UNDEFINED -> false
        else -> false
    }
}

fun Context.dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
fun Context.pxToDp(px: Int): Float = px / (resources.displayMetrics.densityDpi / 160f)
