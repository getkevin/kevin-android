package eu.kevin.accounts.linkingsession.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class AccountLinkingSessionData(
    val selectedCountry: String? = null,
    val selectedBank: String? = null,
    val authorization: String? = null,
) : Parcelable