package eu.kevin.accounts.countryselection.managers

interface CountriesManager {
    suspend fun getSupportedCountries(authState: String): List<String>
}