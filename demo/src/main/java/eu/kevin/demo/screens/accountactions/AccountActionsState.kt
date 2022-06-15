package eu.kevin.demo.screens.accountactions

import android.os.Parcelable
import eu.kevin.common.architecture.interfaces.IState
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class AccountActionsState(
    val bankName: String = ""
) : IState, Parcelable