package eu.kevin.accounts.accountsession.entities

import android.os.Parcelable
import eu.kevin.accounts.accountsession.enums.AccountLinkingType
import eu.kevin.accounts.bankselection.entities.Bank
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class AccountSessionData(
    val selectedCountry: String? = null,
    val selectedBank: Bank? = null,
    val authorization: String? = null,
    val linkingType: AccountLinkingType? = null
) : Parcelable