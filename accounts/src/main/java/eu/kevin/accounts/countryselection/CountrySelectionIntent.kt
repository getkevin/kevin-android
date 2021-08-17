package eu.kevin.accounts.countryselection

import eu.kevin.common.architecture.interfaces.IIntent

internal sealed class CountrySelectionIntent : IIntent {
    data class Initialize(val configuration: CountrySelectionFragmentConfiguration) : CountrySelectionIntent()
    data class HandleCountrySelection(val iso: String) : CountrySelectionIntent()
}