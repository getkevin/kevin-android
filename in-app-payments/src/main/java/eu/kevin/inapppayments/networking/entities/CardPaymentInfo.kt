package eu.kevin.inapppayments.networking.entities

import kotlinx.serialization.Serializable

@Serializable
data class CardPaymentInfo(
    val amount: Double,
    val currencyCode: String
)