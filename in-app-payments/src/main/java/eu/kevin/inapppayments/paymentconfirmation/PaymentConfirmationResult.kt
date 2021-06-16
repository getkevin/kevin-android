package eu.kevin.inapppayments.paymentconfirmation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentConfirmationResult(
    val paymentId: String
) : Parcelable