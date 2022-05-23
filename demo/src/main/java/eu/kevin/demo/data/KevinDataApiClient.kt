package eu.kevin.demo.data

import eu.kevin.demo.data.entities.GetCountriesResponse
import eu.kevin.demo.data.entities.GetCreditorsResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class KevinDataApiClient(private val httpClient: HttpClient) : KevinDataClient {

    override suspend fun getSupportedCountries(): GetCountriesResponse {
        return httpClient.get("countries")
    }

    override suspend fun getCreditorsByCountry(countryCode: String): GetCreditorsResponse {
        return httpClient.get("creditors") {
            parameter("countryCode", countryCode)
        }
    }
}