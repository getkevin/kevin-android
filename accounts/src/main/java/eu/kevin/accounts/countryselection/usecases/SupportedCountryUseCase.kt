package eu.kevin.accounts.countryselection.usecases

import eu.kevin.accounts.countryselection.managers.CountriesManager
import eu.kevin.core.enums.KevinCountry

internal class SupportedCountryUseCase(
    private val countriesManager: CountriesManager
) {

    suspend fun getSupportedCountries(authState: String, filter: List<KevinCountry>): List<String> {
        val apiCountries = countriesManager.getSupportedCountries(authState).map {
            it.lowercase()
        }
        return if (filter.isNotEmpty()) {
            val filterIsos = filter.map { it.iso }
            apiCountries.filter {
                filterIsos.contains(it)
            }
        } else {
            apiCountries
        }
    }
}