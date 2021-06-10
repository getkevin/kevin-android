package eu.kevin.accounts.bankselection

import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import eu.kevin.accounts.bankselection.BankSelectionIntent.*
import eu.kevin.accounts.bankselection.exceptions.BankNotSelectedException
import eu.kevin.accounts.bankselection.factories.BankListItemFactory
import eu.kevin.accounts.bankselection.managers.BanksManager
import eu.kevin.accounts.countryselection.CountrySelectionFragment
import eu.kevin.accounts.countryselection.CountrySelectionFragmentConfiguration
import eu.kevin.accounts.countryselection.managers.CountriesManager
import eu.kevin.accounts.bankselection.entities.Bank
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
                val apiBanks = banksManager.getSupportedBanks(selectedCountry, configuration.authState)

                val banks = apiBanks.map {
                    Bank(it.id, it.name, it.officialName, it.imageUri, it.bic)
                }

                updateState {
                    it.copy(
                        selectedCountry = selectedCountry,
                        loadingState = LoadingState.Loading(false),
                        bankListItems = BankListItemFactory.getBankList(apiBanks, configuration.selectedBankId),
                        banks = banks
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
                bankListItems = state.value.bankListItems.map {
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
                val apiBanks = banksManager.getSupportedBanks(selectedCountry, configuration.authState)
                val banks = apiBanks.map {
                    Bank(it.id, it.name, it.officialName, it.imageUri, it.bic)
                }

                updateState {
                    it.copy(
                        selectedCountry = selectedCountry,
                        loadingState = LoadingState.Loading(false),
                        bankListItems = BankListItemFactory.getBankList(apiBanks),
                        banks = banks
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

        val selectedBank = state.value.bankListItems.firstOrNull { it.isSelected }
        if (selectedBank == null) {
            updateState {
                it.copy(
                    loadingState = LoadingState.Failure(BankNotSelectedException())
                )
            }
            return
        }
        GlobalRouter.returnFragmentResult(
            BankSelectionFragment.Contract,
            state.value.banks.first { it.id == selectedBank.bankId }
        )
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