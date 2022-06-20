package eu.kevin.demo.screens.accountlinking

import android.os.Parcelable
import eu.kevin.common.architecture.interfaces.IState
import eu.kevin.demo.data.database.entities.LinkedAccount
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class AccountLinkingState(
    val isLoading: Boolean = false,
    val linkedAccounts: List<LinkedAccount> = emptyList()
) : IState, Parcelable