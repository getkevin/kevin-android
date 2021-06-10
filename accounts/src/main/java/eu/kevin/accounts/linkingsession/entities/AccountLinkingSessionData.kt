package eu.kevin.accounts.linkingsession.entities

import android.os.Parcelable
import eu.kevin.accounts.bankselection.entities.Bank
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class AccountLinkingSessionData(
    val selectedCountry: String? = null,
    val selectedBank: Bank? = null,
    val authorization: String? = null,
) : Parcelable