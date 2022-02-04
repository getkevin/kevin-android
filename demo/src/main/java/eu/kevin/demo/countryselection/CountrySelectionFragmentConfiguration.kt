package eu.kevin.demo.countryselection
import android.os.Parcelable
import eu.kevin.core.enums.KevinCountry
import kotlinx.parcelize.Parcelize

@Parcelize
class CountrySelectionFragmentConfiguration(
    val selectedCountry: String?
) : Parcelable