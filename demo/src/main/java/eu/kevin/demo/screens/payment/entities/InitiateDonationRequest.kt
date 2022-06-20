package eu.kevin.demo.screens.payment.entities

import eu.kevin.demo.screens.paymenttype.enums.DemoPaymentType

internal data class InitiateDonationRequest(
    val email: String,
    val amount: String,
    val iban: String,
    val creditorName: String,
    val demoPaymentType: DemoPaymentType
)