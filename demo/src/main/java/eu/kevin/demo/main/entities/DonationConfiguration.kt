package eu.kevin.demo.main.entities

import eu.kevin.inapppayments.paymentsession.enums.PaymentType
import java.math.BigDecimal
import java.math.RoundingMode

data class DonationConfiguration(
    var selectedCreditor: CreditorListItem? = null,
    var paymentType: PaymentType = PaymentType.BANK
)