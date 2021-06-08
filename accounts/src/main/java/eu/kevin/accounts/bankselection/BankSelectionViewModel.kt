package eu.kevin.accounts.bankselection

import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import eu.kevin.accounts.bankselection.BankSelectionIntent.*
import eu.kevin.accounts.bankselection.entities.Bank
import eu.kevin.accounts.bankselection.exceptions.BankNotSelectedException
import eu.kevin.accounts.bankselection.managers.BanksManager
import eu.kevin.accounts.countryselection.CountrySelectionFragment
import eu.kevin.accounts.countryselection.CountrySelectionFragmentConfiguration
import eu.kevin.accounts.countryselection.managers.CountriesManager
import eu.kevin.accounts.networking.AccountsClientProvider
import eu.kevin.core.architecture.BaseViewModel
import eu.kevin.core.architecture.routing.GlobalRouter
import eu.kevin.core.entities.LoadingState
import eu.kevin.core.entities.isLoading
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BankSelectionViewModel constructor(
    private val banksManager: BanksManager,
    private val countriesManager: CountriesManager,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<BankSelectionState, BankSelectionIntent>(savedStateHandle) {

    override fun getInitialData() = BankSelectionState()

    override suspend fun handleIntent(intent: BankSelectionIntent) {
        when (intent) {
            is Initialize -> initialize(intent.configuration)
            is HandleBankSelection -> handleBankSelection(intent.bankId)
            is HandleCountrySelectionClick -> handleCountrySelectionClick(intent.configuration)
            is HandleCountrySelected -> handleCountrySelected(intent.selectedCountry, intent.configuration)
            is HandleBackClicked -> {
                if (!state.value.loadingState.isLoading()) {
                    GlobalRouter.popCurrentFragment()
                }
            }
            is HandleContinueClicked -> handleContinueClicked()
        }
    }

    private suspend fun initialize(configuration: BankSelectionFragmentConfiguration) {
        getSavedState()?.let { savedState ->
            updateState { savedState }
            return
        }
        updateState {
            it.copy(
                loadingState = LoadingState.Loading(true)
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val supportedCountries = countriesManager.getSupportedCountries(configuration.authState)
                var selectedCountry = supportedCountries.firstOrNull { it == configuration.selectedCountry }
                if (selectedCountry == null) {
                    selectedCountry = supportedCountries.first()
                }
                val supportedBanks = banksManager.getSupportedBanks(selectedCountry, configuration.authState)
                    .sortedBy { it.officialName }
                    .map {
                        Bank(it.id, it.officialName ?: "", it.imageUri)
                    }

                val selectedBank = supportedBanks.firstOrNull { it.bankId == configuration.selectedBankId }
                if (selectedBank != null) {
                    supportedBanks.firstOrNull { it.bankId == configuration.selectedBankId }?.isSelected = true
                } else {
                    supportedBanks.firstOrNull()?.isSelected = true
                }

                updateState {
                    it.copy(
                        selectedCountry = selectedCountry,
                        loadingState = LoadingState.Loading(false),
                        supportedBanks = supportedBanks
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

    private suspend fun handleBankSelection(bankId: String) {
        updateState { oldState ->
            oldState.copy(
                supportedBanks = state.value.supportedBanks.map {
                    it.copy(isSelected = it.bankId == bankId)
                }
            )
        }
    }

    private fun handleCountrySelectionClick(configuration: BankSelectionFragmentConfiguration) {
        GlobalRouter.pushModalFragment(CountrySelectionFragment().also {
            it.configuration = CountrySelectionFragmentConfiguration(state.value.selectedCountry, configuration.authState)
        })
    }

    private suspend fun handleCountrySelected(selectedCountry: String, configuration: BankSelectionFragmentConfiguration) {
        updateState {
            it.copy(
                loadingState = LoadingState.Loading(true)
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val supportedBanks = banksManager.getSupportedBanks(selectedCountry, configuration.authState)
                    .sortedBy { it.officialName }
                    .map {
                        Bank(it.id, it.officialName ?: "", it.imageUri)
                    }

                supportedBanks.firstOrNull()?.isSelected = true

                updateState {
                    it.copy(
                        selectedCountry = selectedCountry,
                        loadingState = LoadingState.Loading(false),
                        supportedBanks = supportedBanks
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

    private suspend fun handleContinueClicked() {
        if (state.value.loadingState.isLoading()) return

        val selectedBank = state.value.supportedBanks.firstOrNull { it.isSelected }
        if (selectedBank == null) {
            updateState {
                it.copy(
                    loadingState = LoadingState.Failure(BankNotSelectedException())
                )
            }
            return
        }
        GlobalRouter.returnFragmentResult(BankSelectionFragment.Contract, selectedBank.bankId)
    }

    class Factory(owner: SavedStateRegistryOwner) : AbstractSavedStateViewModelFactory(owner, null) {
        override fun <T : ViewModel?> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return BankSelectionViewModel(
                BanksManager(
                    kevinAccountsClient = AccountsClientProvider.kevinAccountsClient
                ),
                CountriesManager(
                    kevinAccountsClient = AccountsClientProvider.kevinAccountsClient
                ),
                handle
            ) as T
        }
    }
}