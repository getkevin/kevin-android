package eu.kevin.accounts.accountlinking

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class AccountLinkingFragmentConfiguration(
    val state: String,
    val selectedBankId: String
) : Parcelable