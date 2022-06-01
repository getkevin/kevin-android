package eu.kevin.accounts.countryselection

import android.os.Parcelable
import eu.kevin.core.enums.KevinCountry
import kotlinx.parcelize.Parcelize

@Parcelize
class CountrySelectionFragmentConfiguration(
    val selectedCountry: String?,
    val countryFilter: List<KevinCountry>,
    val authState: String
) : Parcelable