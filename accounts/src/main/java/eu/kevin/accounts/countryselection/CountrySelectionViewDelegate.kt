package eu.kevin.accounts.countryselection

internal interface CountrySelectionViewDelegate {
    fun onBackClicked()
    fun onCountryClicked(iso: String)
}