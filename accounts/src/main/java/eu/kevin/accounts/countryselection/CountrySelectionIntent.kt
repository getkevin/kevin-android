package eu.kevin.accounts.countryselection

import eu.kevin.core.architecture.interfaces.IIntent

sealed class CountrySelectionIntent : IIntent {
    data class Initialize(val configuration: CountrySelectionFragmentConfiguration) : CountrySelectionIntent()
    data class HandleCountrySelection(val iso: String) : CountrySelectionIntent()
}