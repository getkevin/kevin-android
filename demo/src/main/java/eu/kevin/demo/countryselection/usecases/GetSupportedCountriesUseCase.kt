package eu.kevin.demo.countryselection.usecases

import eu.kevin.demo.countryselection.entities.Country
import eu.kevin.demo.data.KevinDataClient

internal class GetSupportedCountriesUseCase(
    private val kevinDataClient: KevinDataClient
) {
    suspend fun getSupportedCountries(): List<Country> {
        return kevinDataClient.getSupportedCountries().data
            .sortedBy { it }
            .map {
                Country(it)
            }
    }
}