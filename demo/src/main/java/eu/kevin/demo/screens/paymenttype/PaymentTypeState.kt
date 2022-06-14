package eu.kevin.demo.screens.paymenttype

import android.os.Parcelable
import eu.kevin.common.architecture.interfaces.IState
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentTypeState(
    val showLinkedAccountOption: Boolean = false
) : IState, Parcelable