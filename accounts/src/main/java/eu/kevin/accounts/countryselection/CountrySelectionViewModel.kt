package eu.kevin.accounts.countryselection

import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import eu.kevin.accounts.countryselection.CountrySelectionIntent.*
import eu.kevin.accounts.countryselection.entities.Country
import eu.kevin.accounts.countryselection.managers.CountriesManager
import eu.kevin.accounts.networking.AccountsClientProvider
import eu.kevin.core.architecture.BaseViewModel
import eu.kevin.core.architecture.routing.GlobalRouter
import eu.kevin.core.entities.LoadingState
import eu.kevin.core.entities.isLoading
import kotlinx.coroutines.launch

class CountrySelectionViewModel constructor(
    private val countriesManager: CountriesManager,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<CountrySelectionState, CountrySelectionIntent>(savedStateHandle) {

    override fun getInitialData() = CountrySelectionState()

    override suspend fun handleIntent(intent: CountrySelectionIntent) {
        when (intent) {
            is Initialize -> initialize(intent.configuration)
            is HandleBackClicked -> {
                if (!state.value.loadingState.isLoading()) {
                    GlobalRouter.popCurrentFragment()
                }
            }
            is HandleCountrySelection -> handleCountrySelection(intent.iso)
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
        viewModelScope.launch {
            try {
                val supportedCountries = getSupportedCountries(configuration)
                    .sortedBy { it }
                    .map {
                        Country(it)
                    }

                val selectedCountry = supportedCountries.firstOrNull { it.iso == configuration.selectedCountry }
                if (selectedCountry != null) {
                    supportedCountries.firstOrNull { it.iso == configuration.selectedCountry }?.isSelected = true
                } else {
                    supportedCountries.firstOrNull()?.isSelected = true
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
        GlobalRouter.returnFragmentResult(CountrySelectionFragment.Contract, selectedCountry!!.iso)
    }

    private suspend fun getSupportedCountries(configuration: CountrySelectionFragmentConfiguration): List<String> {
        val apiCountries = countriesManager.getSupportedCountries(configuration.authState).map {
            it.lowercase()
        }
        return if (configuration.countryFilter.isNotEmpty()) {
            val filterIsos = configuration.countryFilter.map { it.iso }
            apiCountries.filter {
                filterIsos.contains(it)
            }
        } else {
            apiCountries
        }
    }

    class Factory(owner: SavedStateRegistryOwner) : AbstractSavedStateViewModelFactory(owner, null) {
        override fun <T : ViewModel?> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return CountrySelectionViewModel(
                CountriesManager(
                    kevinAccountsClient = AccountsClientProvider.kevinAccountsClient
                ),
                handle
            ) as T
        }
    }
}