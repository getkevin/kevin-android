package eu.kevin.sample.samples.payment.card

import eu.kevin.sample.networking.entities.payments.Creditor
import java.util.UUID

internal data class CardPaymentUiState(
    val isLoading: Boolean = false,
    val paymentId: UUID? = null,
    val paymentCreditor: Creditor? = null,
    val userMessage: String? = null
)