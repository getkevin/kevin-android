package eu.kevin.inapppayments.paymentsession

import android.os.Parcelable
import eu.kevin.inapppayments.paymentsession.enums.PaymentType
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class PaymentSessionData(
    val selectedPaymentType: PaymentType? = null,
    val selectedCountry: String? = null,
    val selectedBankId: String? = null
) : Parcelable