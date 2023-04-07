package com.example.payments_card.networking.services

import com.example.payments_card.networking.entities.InitiatePaymentRequest
import com.example.payments_card.networking.entities.InitiatePaymentResponse
import retrofit2.http.Body
import retrofit2.http.POST

internal interface KevinApiService {

    @POST("payments/card/")
    suspend fun initiateCardPayment(
        @Body request: InitiatePaymentRequest,
    ): InitiatePaymentResponse
}
