package eu.kevin.accounts.bankselection

import android.os.Parcelable
import eu.kevin.core.enums.KevinCountry
import kotlinx.parcelize.Parcelize

@Parcelize
data class BankSelectionFragmentConfiguration(
    val selectedCountry: String?,
    val isCountrySelectionDisabled: Boolean,
    val countryFilter: List<KevinCountry>,
    val bankFilter: List<String>,
    val selectedBankId: String?,
    val authState: String,
    val showOnlyAccountLinkingSupportedBanks: Boolean
) : Parcelable