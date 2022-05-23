package eu.kevin.common.extensions

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import java.util.*
import kotlin.math.roundToInt

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

fun Context.getDimensionFromAttr(@AttrRes attribute: Int): Int {
    return TypedValue().let {
        theme.resolveAttribute(attribute, it, false)
        it.getDimension(resources.displayMetrics).roundToInt()
    }
}

fun Context.getBooleanFromAttr(@AttrRes attribute: Int): Boolean {
    return TypedValue().let {
        theme.resolveAttribute(attribute, it, false)
        it.data != 0
    }
}

@ColorInt
fun Context.getColorCompat(@ColorRes res: Int): Int {
    return ContextCompat.getColor(this, res)
}

fun Context.getDrawableCompat(@DrawableRes id: Int): Drawable? {
    return ContextCompat.getDrawable(this, id)
}

@Suppress("DEPRECATION")
fun Context.getCurrentLocale(): Locale {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        resources.configuration.locales.get(0)
    } else {
        resources.configuration.locale
    }
}

fun Context.dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
fun Context.pxToDp(px: Int): Float = px / (resources.displayMetrics.densityDpi / 160f)