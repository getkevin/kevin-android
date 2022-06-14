package eu.kevin.demo.screens.countryselection

internal interface CountrySelectionViewDelegate {
    fun onCountryClicked(iso: String)
}