package com.example.payments_bank.networking.api

import com.example.payments_bank.networking.entities.InitiatePaymentRequest
import com.example.payments_bank.networking.services.KevinApiService
import java.util.UUID

internal class KevinApi(
    private val service: KevinApiService
) {

    suspend fun initiateBankPayment(request: InitiatePaymentRequest): UUID {
        val paymentId = service.initiateBankPayment(request).id
        return UUID.fromString(paymentId)
    }
}