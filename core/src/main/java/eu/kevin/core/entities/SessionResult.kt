package eu.kevin.core.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Sealed class representing different results that session could return
 * @param T type of data returned from session contract
 */
sealed class SessionResult<out T : Parcelable> : Parcelable {

    /**
     * Class representing success state of session contract
     * @property value returned data
     * @param T type of returned data
     */
    @Parcelize
    class Success<out T : Parcelable>(
        val value: T
    ) : SessionResult<T>()

    /**
     * Class representing canceled state of session contract.
     * This will be returned when session was finished by user before the final step
     */
    @Parcelize
    object Canceled : SessionResult<Nothing>()

    /**
     * Class representing failure state of session contract
     */
    @Parcelize
    data class Failure(
        val error: Throwable
    ) : SessionResult<Nothing>()
}