package eu.kevin.demo.payment.entities

import eu.kevin.inapppayments.paymentsession.enums.PaymentType

data class InitiateDonationRequest(
    val email: String,
    val amount: String,
    val iban: String,
    val creditorName: String,
    val paymentType: PaymentType
)