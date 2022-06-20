package eu.kevin.demo.auth.entities

import kotlinx.serialization.Serializable

@Serializable
internal data class InitiatePaymentRequest(
    val amount: String,
    val email: String,
    val iban: String,
    val creditorName: String,
    val redirectUrl: String
)