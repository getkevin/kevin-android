package eu.kevin.accounts.countryselection

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class CountrySelectionFragmentConfiguration(
    val selectedCountry: String?,
    val authState: String
) : Parcelable