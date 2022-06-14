package eu.kevin.demo.screens.countryselection

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import eu.kevin.common.architecture.BaseViewModel
import eu.kevin.common.entities.LoadingState
import eu.kevin.demo.data.ClientProvider
import eu.kevin.demo.screens.countryselection.usecases.GetSupportedCountriesUseCase
import eu.kevin.demo.routing.DemoRouter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class CountrySelectionViewModel constructor(
    private val getSupportedCountriesUseCase: GetSupportedCountriesUseCase,
    private val ioDispatcher: CoroutineDispatcher,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<CountrySelectionState, CountrySelectionIntent>(savedStateHandle) {

    override fun getInitialData() = CountrySelectionState()

    override suspend fun handleIntent(intent: CountrySelectionIntent) {
        when (intent) {
            is CountrySelectionIntent.Initialize -> initialize(intent.configuration)
            is CountrySelectionIntent.HandleCountrySelection -> handleCountrySelection(intent.iso)
        }
    }

    private suspend fun initialize(configuration: CountrySelectionFragmentConfiguration) {
        getSavedState()?.let { savedState ->
            updateState { savedState }
            return
        }
        updateState {
            it.copy(
                loadingState = LoadingState.Loading(true)
            )
        }
        viewModelScope.launch(ioDispatcher) {
            try {
                val supportedCountries = getSupportedCountriesUseCase.getSupportedCountries()

                val selectedCountry = supportedCountries
                    .firstOrNull { it.iso == configuration.selectedCountry }
                if (selectedCountry != null) {
                    supportedCountries
                        .firstOrNull { it.iso == configuration.selectedCountry }?.isSelected = true
                } else {
                    supportedCountries
                        .firstOrNull()?.isSelected = true
                }

                updateState {
                    it.copy(
                        loadingState = LoadingState.Loading(false),
                        supportedCountries = supportedCountries
                    )
                }
            } catch (e: Exception) {
                updateState {
                    it.copy(
                        loadingState = LoadingState.Failure(e)
                    )
                }
            }
        }
    }

    private fun handleCountrySelection(countryIso: String) {
        val selectedCountry = state.value.supportedCountries.firstOrNull { it.iso == countryIso }
        DemoRouter.returnFragmentResult(CountrySelectionContract, selectedCountry!!.iso)
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(owner: SavedStateRegistryOwner) :
        AbstractSavedStateViewModelFactory(owner, null) {
        override fun <T : ViewModel?> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return CountrySelectionViewModel(
                GetSupportedCountriesUseCase(
                    kevinDataClient = ClientProvider.kevinApiClient
                ),
                Dispatchers.IO,
                handle
            ) as T
        }
    }
}