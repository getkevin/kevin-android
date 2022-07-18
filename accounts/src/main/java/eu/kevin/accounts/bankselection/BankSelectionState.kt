package eu.kevin.accounts.bankselection

import android.os.Parcelable
import eu.kevin.accounts.bankselection.entities.BankListItem
import eu.kevin.common.architecture.interfaces.State
import eu.kevin.common.entities.LoadingState
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class BankSelectionState(
    val selectedCountry: String = "",
    val isCountrySelectionDisabled: Boolean = true,
    val bankListItems: List<BankListItem> = emptyList(),
    val loadingState: LoadingState? = null
) : State, Parcelable