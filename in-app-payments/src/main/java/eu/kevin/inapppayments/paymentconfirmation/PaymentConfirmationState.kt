package eu.kevin.inapppayments.paymentconfirmation

import android.os.Parcelable
import eu.kevin.common.architecture.interfaces.IState
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class PaymentConfirmationState(
    val url: String = ""
) : IState, Parcelable