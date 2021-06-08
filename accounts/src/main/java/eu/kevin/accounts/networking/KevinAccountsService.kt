package eu.kevin.accounts.networking

import eu.kevin.accounts.networking.entities.ApiBank
import eu.kevin.core.networking.entities.KevinResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface KevinAccountsService {

    @GET("platform/frame/countries/{token}")
    suspend fun getSupportedCountries(
        @Path("token") token: String
    ) : KevinResponse<String>

    @GET("platform/frame/banks/{token}")
    suspend fun getSupportedBanks(
        @Path("token") token: String,
        @Query("countryCode") countryCode: String?
    ) : KevinResponse<ApiBank>
}