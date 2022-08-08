package eu.kevin.accounts.networking

import eu.kevin.accounts.networking.entities.ApiBank
import eu.kevin.core.networking.entities.KevinResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal class KevinAccountsApiClient(
    private val httpClient: HttpClient
) : KevinAccountsClient {

    override suspend fun getSupportedCountries(token: String): KevinResponse<String> {
        return httpClient.get("platform/frame/countries/$token").body()
    }

    override suspend fun getSupportedBanks(token: String, country: String?): KevinResponse<ApiBank> {
        return httpClient.get("platform/frame/banks/$token") {
            parameter("countryCode", country)
        }.body()
    }
}