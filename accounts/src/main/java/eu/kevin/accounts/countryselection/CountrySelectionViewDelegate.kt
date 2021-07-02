package eu.kevin.accounts.countryselection

internal interface CountrySelectionViewDelegate {
    fun onCountryClicked(iso: String)
}