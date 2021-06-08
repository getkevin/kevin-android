package eu.kevin.accounts.accountlinking

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AccountLinkingFragmentResult(
    val requestId: String,
    val authCode: String
) : Parcelable