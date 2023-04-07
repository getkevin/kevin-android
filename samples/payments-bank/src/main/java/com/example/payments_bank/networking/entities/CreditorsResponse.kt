package com.example.payments_bank.networking.entities

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