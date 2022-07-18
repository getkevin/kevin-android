package eu.kevin.demo.screens.countryselection

import eu.kevin.common.architecture.interfaces.Intent

internal sealed class CountrySelectionIntent : Intent {
    data class Initialize(val configuration: CountrySelectionFragmentConfiguration) : CountrySelectionIntent()
    data class HandleCountrySelection(val iso: String) : CountrySelectionIntent()
}