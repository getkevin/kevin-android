package eu.kevin.accounts.countryselection

import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import eu.kevin.accounts.countryselection.CountrySelectionIntent.*
import eu.kevin.accounts.countryselection.entities.Country
import eu.kevin.accounts.countryselection.managers.KevinCountriesManager
import eu.kevin.accounts.countryselection.usecases.SupportedCountryUseCase
import eu.kevin.accounts.networking.AccountsClientProvider
import eu.kevin.common.architecture.BaseViewModel
import eu.kevin.common.architecture.routing.GlobalRouter
import eu.kevin.common.entities.LoadingState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CountrySelectionViewModel constructor(
    private val countryUseCase: SupportedCountryUseCase,
    private val ioDispatcher: CoroutineDispatcher,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<CountrySelectionState, CountrySelectionIntent>(savedStateHandle) {

    override fun getInitialData() = CountrySelectionState()

    override suspend fun handleIntent(intent: CountrySelectionIntent) {
        when (intent) {
            is Initialize -> initialize(intent.configuration)
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
        viewModelScope.launch(ioDispatcher) {
            try {
                val supportedCountries = countryUseCase.getSupportedCountries(configuration.authState, configuration.countryFilter)
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

    class Factory(owner: SavedStateRegistryOwner) : AbstractSavedStateViewModelFactory(owner, null) {
        override fun <T : ViewModel?> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return CountrySelectionViewModel(
                SupportedCountryUseCase(
                    KevinCountriesManager(
                        kevinAccountsClient = AccountsClientProvider.kevinAccountsClient
                    )
                ),
                Dispatchers.IO,
                handle
            ) as T
        }
    }
}