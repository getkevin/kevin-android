package eu.kevin.sample.networking.services

import eu.kevin.sample.networking.entities.payments.CreditorsResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal interface KevinDataApiService {

    @GET("creditors")
    suspend fun getCreditors(
        @Query("countryCode") countryIso: String,
    ): CreditorsResponse
}
