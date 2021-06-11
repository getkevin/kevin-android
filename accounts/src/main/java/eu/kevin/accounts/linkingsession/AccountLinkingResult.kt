package eu.kevin.accounts.linkingsession

import android.os.Parcelable
import eu.kevin.accounts.bankselection.entities.Bank
import kotlinx.parcelize.Parcelize

/**
 * Data class representing data after successful account linking
 * @property linkToken a token representing linked account
 * @property bank linked bank object
 */
@Parcelize
data class AccountLinkingResult(
    val linkToken: String,
    val bank: Bank
) : Parcelable