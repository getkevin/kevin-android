package eu.kevin.demo.main.entities

import eu.kevin.inapppayments.paymentsession.enums.PaymentType

data class DonationRequest(
    val email: String,
    val amount: String,
    val isTermsAccepted: Boolean,
    val paymentType: PaymentType
)