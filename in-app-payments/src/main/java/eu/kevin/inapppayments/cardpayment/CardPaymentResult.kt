package eu.kevin.inapppayments.cardpayment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CardPaymentResult(
    val paymentId: String
) : Parcelable