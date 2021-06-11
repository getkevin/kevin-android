package eu.kevin.accounts.bankselection

import android.os.Parcelable
import eu.kevin.accounts.bankselection.entities.BankListItem
import eu.kevin.core.architecture.interfaces.IState
import eu.kevin.core.entities.LoadingState
import kotlinx.parcelize.Parcelize

@Parcelize
data class BankSelectionState(
    val selectedCountry: String = "",
    val bankListItems: List<BankListItem> = emptyList(),
    val loadingState: LoadingState? = null
) : IState, Parcelable