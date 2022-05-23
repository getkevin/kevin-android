package eu.kevin.inapppayments.paymentsession

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data class representing data after successful payment
 * @property paymentId id of successful payment
 */
@Parcelize
data class PaymentSessionResult(
    val paymentId: String
) : Parcelable