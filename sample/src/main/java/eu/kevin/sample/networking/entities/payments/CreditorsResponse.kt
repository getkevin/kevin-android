package eu.kevin.sample.networking.entities.payments

internal data class CreditorsResponse(
    val data: List<Creditor>
)

internal data class Creditor(
    val name: String,
    val accounts: List<CreditorAccount>
)

internal data class CreditorAccount(
    val currencyCode: String,
    val iban: String
)