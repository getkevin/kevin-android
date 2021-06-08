package eu.kevin.accounts.networking

import eu.kevin.accounts.networking.entities.ApiBank
import eu.kevin.core.networking.BaseApiClient
import eu.kevin.core.networking.entities.KevinResponse

interface KevinAccountsClient : BaseApiClient {

    /**
     * @param token state representing account linking session
     *
     * @return list of country iso codes
     */
    suspend fun getSupportedCountries(token: String): KevinResponse<String>

    /**
     * @param token state representing account linking session
     * @param country country iso code of which banks should be returned
     *
     * @return list of [ApiBank]. If country is null, this will return all supported banks of all
     * supported countries
     */
    suspend fun getSupportedBanks(token: String, country: String?): KevinResponse<ApiBank>
}