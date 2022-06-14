package eu.kevin.demo.screens.countryselection

import android.os.Parcelable
import eu.kevin.common.architecture.interfaces.IState
import eu.kevin.common.entities.LoadingState
import eu.kevin.demo.screens.countryselection.entities.Country
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class CountrySelectionState(
    val supportedCountries: List<Country> = emptyList(),
    val loadingState: LoadingState? = null
) : IState, Parcelable