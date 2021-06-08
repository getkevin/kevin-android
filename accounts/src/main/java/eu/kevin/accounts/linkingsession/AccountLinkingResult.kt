package eu.kevin.accounts.linkingsession

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data class representing data after successful account linking
 * @property linkToken a token representing linked account
 */
@Parcelize
data class AccountLinkingResult(
    val linkToken: String
) : Parcelable