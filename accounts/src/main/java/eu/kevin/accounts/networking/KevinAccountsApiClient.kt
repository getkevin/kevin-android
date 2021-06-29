package eu.kevin.accounts.networking

import eu.kevin.accounts.networking.entities.ApiBank
import eu.kevin.core.networking.entities.KevinResponse
import io.ktor.client.*
import io.ktor.client.request.*

internal class KevinAccountsApiClient(
    private val httpClient: HttpClient
) : KevinAccountsClient {

    override suspend fun getSupportedCountries(token: String): KevinResponse<String> {
        return httpClient.get("platform/frame/countries/${token}")
    }

    override suspend fun getSupportedBanks(token: String, country: String?): KevinResponse<ApiBank> {
        return httpClient.get("platform/frame/banks/${token}") {
            parameter("countryCode", country)
        }
    }
}