package eu.kevin.demo.countryselection

internal interface CountrySelectionViewDelegate {
    fun onCountryClicked(iso: String)
}