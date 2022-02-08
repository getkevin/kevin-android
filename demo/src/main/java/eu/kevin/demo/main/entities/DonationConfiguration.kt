package eu.kevin.demo.main.entities

import eu.kevin.inapppayments.paymentsession.enums.PaymentType
import java.math.BigDecimal
import java.math.RoundingMode


data class DonationConfiguration(
    var email: String = "",
    var termsAgreed: Boolean = false,
    var amount: String = "",
    var selectedCreditor: CreditorListItem? = null,
    var paymentType: PaymentType = PaymentType.BANK
) {

    fun getAmount(): BigDecimal {
        return try {
            BigDecimal(amount)
        } catch (e: Throwable) {
            BigDecimal("0")
        }
    }

    fun getAmountText(): String {
        return getAmount().setScale(2, RoundingMode.HALF_EVEN).toString()
    }

    fun canProceed() =
        email.isNotBlank() && termsAgreed && amount.isNotBlank() && selectedCreditor != null
}