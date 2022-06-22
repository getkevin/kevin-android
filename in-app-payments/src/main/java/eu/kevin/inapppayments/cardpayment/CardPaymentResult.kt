package eu.kevin.inapppayments.cardpayment

import android.os.Parcelable
import eu.kevin.inapppayments.common.enums.PaymentStatus
import kotlinx.parcelize.Parcelize

@Parcelize
data class CardPaymentResult(
    val paymentId: String,
    val status: PaymentStatus
) : Parcelable