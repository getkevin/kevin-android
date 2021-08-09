package eu.kevin.accounts.accountsession

import android.os.Parcelable
import eu.kevin.accounts.bankselection.entities.Bank
import kotlinx.parcelize.Parcelize

/**
 * Data class representing data after successful account linking
 * @property linkToken a token representing linked account
 * @property bank linked bank object
 */
@Parcelize
data class AccountSessionResult(
    val linkToken: String,
    val bank: Bank
) : Parcelable