package eu.kevin.core.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Sealed class representing different results that activity result contract could return
 * @param T type of data returned from activity result contract
 */
sealed class ActivityResult<out T : Parcelable> : Parcelable {

    /**
     * Class representing success state of activity result contract
     * @property value returned data
     * @param T type of returned data
     */
    @Parcelize
    class Success<out T : Parcelable>(
        val value: T
    ) : ActivityResult<T>()

    /**
     * Class representing canceled state of activity result contract.
     * This will be returned when activity contract was finished by user before the final step
     */
    @Parcelize
    object Canceled: ActivityResult<Nothing>()

    /**
     * Class representing failure state of activity result contract
     */
    @Parcelize
    data class Failure(
        val error: Throwable
    ) : ActivityResult<Nothing>()
}