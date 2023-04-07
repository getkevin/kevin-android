package com.example.payments_bank.networking.services

import com.example.payments_bank.networking.entities.CreditorsResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal interface KevinDataApiService {

    @GET("creditors")
    suspend fun getCreditors(
        @Query("countryCode") countryIso: String,
    ): CreditorsResponse
}
