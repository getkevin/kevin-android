package com.example.payments_card.networking.api

import com.example.payments_card.networking.entities.InitiatePaymentRequest
import com.example.payments_card.networking.services.KevinApiService
import java.util.UUID

internal class KevinApi(
    private val service: KevinApiService
) {

    suspend fun initiateCardPayment(request: InitiatePaymentRequest): UUID {
        val paymentId = service.initiateCardPayment(request).id
        return UUID.fromString(paymentId)
    }
}