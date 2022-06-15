package eu.kevin.demo.screens.payment.entities

internal data class DonationRequest(
    val email: String,
    val amount: String,
    val isTermsAccepted: Boolean
)