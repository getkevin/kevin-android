package com.example.payments_card.main

import com.example.payments_card.networking.entities.Creditor
import java.util.UUID

internal data class MainUiState(
    val isLoading: Boolean = false,
    val paymentId: UUID? = null,
    val paymentCreditor: Creditor? = null,
    val userMessage: String? = null
)