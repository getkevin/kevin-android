package eu.kevin.demo.screens.accountactions.entities

import android.os.Parcelable
import eu.kevin.demo.screens.accountactions.enums.AccountActionType
import kotlinx.parcelize.Parcelize

@Parcelize
data class AccountAction(
    val id: Long,
    val accountAction: AccountActionType
) : Parcelable