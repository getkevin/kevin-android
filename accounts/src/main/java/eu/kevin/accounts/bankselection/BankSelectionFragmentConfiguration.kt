package eu.kevin.accounts.bankselection

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BankSelectionFragmentConfiguration(
    val selectedCountry: String?,
    val selectedBankId: String?,
    val authState: String
) : Parcelable