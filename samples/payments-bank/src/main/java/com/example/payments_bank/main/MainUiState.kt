package com.example.payments_bank.main

import com.example.payments_bank.networking.entities.Creditor
import eu.kevin.core.enums.KevinCountry
import java.util.UUID

internal data class MainUiState(
    val isLoading: Boolean = false,
    val paymentId: UUID? = null,
    val paymentCountry: KevinCountry? = null,
    val paymentCreditor: Creditor? = null,
    val paymentStatus: String? = null,
    val userMessage: String? = null
)