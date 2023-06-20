package eu.kevin.common.extensions

import android.os.Build
import android.os.Bundle
import android.os.Parcelable

inline fun <reified T : Parcelable> Bundle.optionalParcelable(key: String): T? =
    when {
        Build.VERSION.SDK_INT >= 33 -> getParcelable(key, T::class.java)
        else -> @Suppress("DEPRECATION") (getParcelable(key) as? T)
    }

inline fun <reified T : Parcelable> Bundle.requireParcelable(key: String): T =
    optionalParcelable(key) ?: throw Error("Parcelable required but was not found!")
