package eu.kevin.accounts.countryselection

import android.os.Parcelable
import eu.kevin.accounts.countryselection.enums.KevinCountry
import kotlinx.parcelize.Parcelize

@Parcelize
class CountrySelectionFragmentConfiguration(
    val selectedCountry: String?,
    val countryFilter: List<KevinCountry>,
    val authState: String
) : Parcelable