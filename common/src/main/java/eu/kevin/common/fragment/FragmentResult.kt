package eu.kevin.common.fragment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class FragmentResult<out T : Parcelable> : Parcelable {
    @Parcelize
    class Success<out T : Parcelable>(
        val value: T
    ) : FragmentResult<T>()

    @Parcelize
    object Canceled : FragmentResult<Nothing>()

    @Parcelize
    data class Failure(
        val error: Throwable
    ) : FragmentResult<Nothing>()
}