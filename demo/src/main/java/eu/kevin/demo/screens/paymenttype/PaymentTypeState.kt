package eu.kevin.demo.screens.paymenttype

import android.os.Parcelable
import eu.kevin.common.architecture.interfaces.IState
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class PaymentTypeState(
    val showLinkedAccountOption: Boolean = false
) : IState, Parcelable