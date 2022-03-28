package eu.kevin.accounts.accountlinking

import android.os.Parcelable
import eu.kevin.accounts.accountsession.enums.AccountLinkingType
import kotlinx.parcelize.Parcelize

@Parcelize
data class AccountLinkingFragmentConfiguration(
    val state: String,
    val selectedBankId: String? = null,
    val linkingType: AccountLinkingType
) : Parcelable