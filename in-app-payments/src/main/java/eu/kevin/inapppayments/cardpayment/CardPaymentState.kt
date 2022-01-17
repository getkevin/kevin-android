package eu.kevin.inapppayments.cardpayment

import android.os.Parcelable
import eu.kevin.common.architecture.interfaces.IState
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class CardPaymentState(
    val url: String = ""
) : IState, Parcelable