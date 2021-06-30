package eu.kevin.core.extensions

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.*
import androidx.core.content.ContextCompat

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

fun Context.dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
fun Context.pxToDp(px: Int): Float = px / (resources.displayMetrics.densityDpi / 160f)
