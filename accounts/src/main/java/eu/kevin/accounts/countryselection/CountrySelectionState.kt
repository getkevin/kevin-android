package eu.kevin.accounts.countryselection

import android.os.Parcelable
import eu.kevin.accounts.countryselection.entities.Country
import eu.kevin.core.architecture.interfaces.IState
import eu.kevin.core.entities.LoadingState
import kotlinx.parcelize.Parcelize

@Parcelize
data class CountrySelectionState(
    val supportedCountries: List<Country> = emptyList(),
    val loadingState: LoadingState? = null
) : IState, Parcelable