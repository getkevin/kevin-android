package eu.kevin.demo.screens.accountactions

import android.os.Parcelable
import eu.kevin.common.architecture.interfaces.State
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class AccountActionsState(
    val bankName: String = ""
) : State, Parcelable