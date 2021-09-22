package eu.kevin.inapppayments.paymentconfirmation

import android.os.Parcelable
import eu.kevin.inapppayments.paymentsession.enums.PaymentType
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentConfirmationFragmentConfiguration(
    val paymentId: String,
    val paymentType: PaymentType,
    val selectedBank: String? = null,
    val skipAuthentication: Boolean
) : Parcelable