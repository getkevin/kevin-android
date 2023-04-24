package eu.kevin.sample.networking.entities.payments

internal data class InitiatePaymentRequest(
    val amount: String,
    val currencyCode: String,
    val email: String,
    val iban: String,
    val creditorName: String,
    val redirectUrl: String
)