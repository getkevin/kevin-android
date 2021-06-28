package eu.kevin.inapppayments.paymentsession.entities

import android.os.Parcelable
import eu.kevin.accounts.bankselection.entities.Bank
import eu.kevin.inapppayments.paymentsession.enums.PaymentType
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class PaymentSessionData(
    val selectedPaymentType: PaymentType? = null,
    val selectedCountry: String? = null,
    val selectedBank: Bank? = null
) : Parcelable