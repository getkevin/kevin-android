package eu.kevin.demo.countryselection
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class CountrySelectionFragmentConfiguration(
    val selectedCountry: String?
) : Parcelable