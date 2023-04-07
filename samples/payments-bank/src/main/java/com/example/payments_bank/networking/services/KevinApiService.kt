package com.example.payments_bank.networking.services

import com.example.payments_bank.networking.entities.InitiatePaymentRequest
import com.example.payments_bank.networking.entities.InitiatePaymentResponse
import retrofit2.http.Body
import retrofit2.http.POST

internal interface KevinApiService {

    @POST("payments/bank/")
    suspend fun initiateBankPayment(
        @Body request: InitiatePaymentRequest,
    ): InitiatePaymentResponse
}
