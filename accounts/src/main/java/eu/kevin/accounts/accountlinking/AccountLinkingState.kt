package eu.kevin.accounts.accountlinking

import android.os.Parcelable
import eu.kevin.core.architecture.interfaces.IState
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class AccountLinkingState(
    val bankRedirectUrl: String = ""
) : IState, Parcelable