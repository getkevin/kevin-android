package eu.kevin.accounts.bankselection

import android.os.Parcelable
import eu.kevin.accounts.countryselection.enums.KevinCountry
import kotlinx.parcelize.Parcelize

@Parcelize
data class BankSelectionFragmentConfiguration(
    val selectedCountry: String?,
    val isCountrySelectionDisabled: Boolean,
    val countryFilter: List<KevinCountry>,
    val selectedBankId: String?,
    val authState: String
) : Parcelable