package eu.kevin.demo.screens.paymenttype

import android.os.Parcelable
import eu.kevin.common.architecture.interfaces.State
import kotlinx.android.parcel.Parcelize

@Parcelize
internal data class PaymentTypeState(
    val showLinkedAccountOption: Boolean = false
) : State, Parcelable