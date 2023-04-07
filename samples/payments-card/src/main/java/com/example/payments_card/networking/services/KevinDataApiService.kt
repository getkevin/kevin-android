package com.example.payments_card.networking.services

import com.example.payments_card.networking.entities.CreditorsResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal interface KevinDataApiService {

    @GET("creditors")
    suspend fun getCreditors(
        @Query("countryCode") countryIso: String,
    ): CreditorsResponse
}
