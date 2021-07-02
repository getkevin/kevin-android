package eu.kevin.accounts.countryselection.managers

import eu.kevin.accounts.networking.KevinAccountsClient

class KevinCountriesManager(
    private val kevinAccountsClient: KevinAccountsClient
) : CountriesManagerInterface {

    override suspend fun getSupportedCountries(authState: String): List<String> {
        return kevinAccountsClient.getSupportedCountries(authState).data
    }
}