package eu.kevin.demo.data

import eu.kevin.core.networking.BaseApiClient
import eu.kevin.demo.data.entities.GetCountriesResponse
import eu.kevin.demo.data.entities.GetCreditorsResponse

internal interface KevinDataClient : BaseApiClient {
    suspend fun getSupportedCountries(): GetCountriesResponse
    suspend fun getCreditorsByCountry(countryCode: String): GetCreditorsResponse
}