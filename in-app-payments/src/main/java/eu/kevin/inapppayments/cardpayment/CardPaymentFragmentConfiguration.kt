package eu.kevin.inapppayments.cardpayment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CardPaymentFragmentConfiguration(
    val paymentId: String
) : Parcelable