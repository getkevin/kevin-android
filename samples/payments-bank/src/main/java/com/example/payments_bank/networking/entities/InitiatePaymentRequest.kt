package com.example.payments_bank.networking.entities

internal data class InitiatePaymentRequest(
    val amount: String,
    val currencyCode: String,
    val email: String,
    val iban: String,
    val creditorName: String,
    val redirectUrl: String
)