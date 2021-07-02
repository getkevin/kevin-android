package eu.kevin.accounts.countryselection.managers

import eu.kevin.accounts.networking.KevinAccountsClient

internal class KevinCountriesManager(
    private val kevinAccountsClient: KevinAccountsClient
) : CountriesManager {

    override suspend fun getSupportedCountries(authState: String): List<String> {
        return kevinAccountsClient.getSupportedCountries(authState).data
    }
}