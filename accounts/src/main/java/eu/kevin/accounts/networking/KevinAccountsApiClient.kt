package eu.kevin.accounts.networking

import eu.kevin.core.networking.executeRequest

internal class KevinAccountsApiClient(
    private val service: KevinAccountsService
) : KevinAccountsClient {

    override suspend fun getSupportedCountries(token: String) = executeRequest {
        service.getSupportedCountries(token)
    }

    override suspend fun getSupportedBanks(token: String, country: String?) = executeRequest {
        service.getSupportedBanks(token, country)
    }
}