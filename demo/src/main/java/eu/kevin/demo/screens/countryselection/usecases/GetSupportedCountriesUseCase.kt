package eu.kevin.demo.screens.countryselection.usecases

import eu.kevin.demo.screens.countryselection.entities.Country
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