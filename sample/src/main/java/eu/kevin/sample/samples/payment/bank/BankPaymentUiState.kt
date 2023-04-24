package eu.kevin.sample.samples.payment.bank

import eu.kevin.core.enums.KevinCountry
import eu.kevin.sample.networking.entities.payments.Creditor
import java.util.UUID

internal data class BankPaymentUiState(
    val isLoading: Boolean = false,
    val paymentId: UUID? = null,
    val paymentCountry: KevinCountry? = null,
    val paymentCreditor: Creditor? = null,
    val paymentStatus: String? = null,
    val userMessage: String? = null
)