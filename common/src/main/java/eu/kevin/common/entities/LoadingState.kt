package eu.kevin.common.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class LoadingState : Parcelable {

    @Parcelize
    data class Loading(val isLoading: Boolean) : LoadingState()

    @Parcelize
    data class Failure(val error: Throwable) : LoadingState()
}

fun LoadingState?.isLoading(): Boolean = (this as? LoadingState.Loading)?.isLoading == true