package eu.kevin.accounts.countryselection.managers

interface CountriesManagerInterface {
    suspend fun getSupportedCountries(authState: String): List<String>
}