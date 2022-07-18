package eu.kevin.accounts.countryselection

import android.os.Parcelable
import eu.kevin.accounts.countryselection.entities.Country
import eu.kevin.common.architecture.interfaces.State
import eu.kevin.common.entities.LoadingState
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class CountrySelectionState(
    val supportedCountries: List<Country> = emptyList(),
    val loadingState: LoadingState? = null
) : State, Parcelable