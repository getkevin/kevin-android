package eu.kevin.accounts.countryselection.managers

import eu.kevin.accounts.networking.KevinAccountsClient

class CountriesManager(
    private val kevinAccountsClient: KevinAccountsClient
) {
    suspend fun getSupportedCountries(authState: String): List<String> {
        return kevinAccountsClient.getSupportedCountries(authState).data
    }
}