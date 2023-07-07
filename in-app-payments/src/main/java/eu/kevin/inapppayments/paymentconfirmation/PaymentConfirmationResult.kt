package eu.kevin.inapppayments.paymentconfirmation

import android.os.Parcelable
import eu.kevin.inapppayments.enums.PaymentStatus
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentConfirmationResult(
    val paymentId: String,
    val status: PaymentStatus
) : Parcelable