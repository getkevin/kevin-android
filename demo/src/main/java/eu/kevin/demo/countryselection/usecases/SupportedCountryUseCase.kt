package eu.kevin.demo.countryselection.usecases

import eu.kevin.demo.data.KevinDataClient

internal class SupportedCountryUseCase(
    private val kevinDataClient: KevinDataClient
) {

    suspend fun getSupportedCountries(): List<String> {
        return kevinDataClient.getSupportedCountries().data
    }
}