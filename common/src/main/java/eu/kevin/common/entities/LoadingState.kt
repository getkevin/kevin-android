package eu.kevin.common.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class LoadingState : Parcelable {
    @Parcelize
    data class Loading(val isLoading: Boolean) : LoadingState()

    @Parcelize
    object Success: LoadingState()

    @Parcelize
    data class Failure(val error: Throwable) : LoadingState()

    @Parcelize
    data class FailureWithMessage(val message: String) : LoadingState()
}
fun LoadingState?.isLoading(): Boolean = (this as? LoadingState.Loading)?.isLoading == true