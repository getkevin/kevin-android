package eu.kevin.accounts.accountsession

import android.os.Parcelable
import eu.kevin.accounts.accountsession.enums.AccountLinkingType
import eu.kevin.accounts.bankselection.entities.Bank
import kotlinx.parcelize.Parcelize

/**
 * Data class representing data after successful account linking
 * @property authorizationCode a code which can be used to fetch linked bank tokens
 * @property bank linked bank object
 * @property linkingType linked type
 */
@Parcelize
data class AccountSessionResult(
    val authorizationCode: String,
    val bank: Bank?,
    val linkingType: AccountLinkingType
) : Parcelable