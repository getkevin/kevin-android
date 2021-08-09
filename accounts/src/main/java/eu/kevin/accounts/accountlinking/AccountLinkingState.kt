package eu.kevin.accounts.accountlinking

import android.os.Parcelable
import eu.kevin.common.architecture.interfaces.IState
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class AccountLinkingState(
    val bankRedirectUrl: String = ""
) : IState, Parcelable