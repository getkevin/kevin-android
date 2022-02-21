package eu.kevin.demo.data

import eu.kevin.demo.data.entities.GetCountriesResponse
import eu.kevin.demo.data.entities.GetCreditorsResponse
import io.ktor.client.*
import io.ktor.client.request.*

class KevinDataApiClient (private val httpClient: HttpClient) : KevinDataClient {

    override suspend fun getSupportedCountries(): GetCountriesResponse {
        return httpClient.get("countries")
    }

    override suspend fun getCreditorsByCountry(countryCode: String): GetCreditorsResponse {
        return httpClient.get("creditors") {
            parameter("countryCode", countryCode)
        }
    }
}