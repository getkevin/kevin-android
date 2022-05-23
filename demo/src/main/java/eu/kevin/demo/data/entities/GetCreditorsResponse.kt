package eu.kevin.demo.data.entities

import kotlinx.serialization.Serializable

@Serializable
data class GetCreditorsResponse(
    val data: List<Creditor>
)

@Serializable
data class Creditor(
    val logo: String,
    val name: String,
    val iban: String
)