package eu.kevin.accounts.accountlinking

import android.os.Parcelable
import eu.kevin.accounts.accountsession.enums.AccountLinkingType
import eu.kevin.common.architecture.interfaces.IState
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class AccountLinkingState(
    val accountLinkingType: AccountLinkingType = AccountLinkingType.BANK,
    val isProcessing: Boolean = false
) : IState, Parcelable