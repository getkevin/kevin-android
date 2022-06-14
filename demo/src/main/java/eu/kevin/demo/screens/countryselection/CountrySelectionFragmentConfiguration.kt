package eu.kevin.demo.screens.countryselection
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class CountrySelectionFragmentConfiguration(
    val selectedCountry: String?
) : Parcelable