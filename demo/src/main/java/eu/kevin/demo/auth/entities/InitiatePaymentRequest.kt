package eu.kevin.demo.auth.entities

import kotlinx.serialization.Serializable

@Serializable
data class InitiatePaymentRequest(
    val amount: String
)