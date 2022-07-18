package eu.kevin.accounts.accountlinking

import android.os.Parcelable
import eu.kevin.accounts.accountsession.enums.AccountLinkingType
import eu.kevin.common.architecture.interfaces.State
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class AccountLinkingState(
    val bankRedirectUrl: String = "",
    val accountLinkingType: AccountLinkingType = AccountLinkingType.BANK
) : State, Parcelable