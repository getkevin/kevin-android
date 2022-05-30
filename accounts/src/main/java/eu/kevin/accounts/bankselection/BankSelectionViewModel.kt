package eu.kevin.accounts.bankselection

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import eu.kevin.accounts.bankselection.BankSelectionIntent.HandleBackClicked
import eu.kevin.accounts.bankselection.BankSelectionIntent.HandleBankSelection
import eu.kevin.accounts.bankselection.BankSelectionIntent.HandleContinueClicked
import eu.kevin.accounts.bankselection.BankSelectionIntent.HandleCountrySelected
import eu.kevin.accounts.bankselection.BankSelectionIntent.HandleCountrySelectionClick
import eu.kevin.accounts.bankselection.BankSelectionIntent.Initialize
import eu.kevin.accounts.bankselection.entities.Bank
import eu.kevin.accounts.bankselection.entities.SupportedBanksFilter
import eu.kevin.accounts.bankselection.exceptions.BankNotSelectedException
import eu.kevin.accounts.bankselection.factories.BankListItemFactory
import eu.kevin.accounts.bankselection.managers.KevinBankManager
import eu.kevin.accounts.bankselection.usecases.GetSupportedBanksUseCase
import eu.kevin.accounts.countryselection.CountrySelectionContract
import eu.kevin.accounts.countryselection.CountrySelectionFragmentConfiguration
import eu.kevin.accounts.countryselection.managers.KevinCountriesManager
import eu.kevin.accounts.countryselection.usecases.SupportedCountryUseCase
import eu.kevin.accounts.networking.AccountsClientProvider
import eu.kevin.common.architecture.BaseViewModel
import eu.kevin.common.architecture.routing.GlobalRouter
import eu.kevin.common.dispatchers.CoroutineDispatchers
import eu.kevin.common.dispatchers.DefaultCoroutineDispatchers
import eu.kevin.common.entities.LoadingState
import eu.kevin.common.entities.isLoading
import kotlinx.coroutines.launch

internal class BankSelectionViewModel constructor(
    private val countryUseCase: SupportedCountryUseCase,
    private val banksUseCase: GetSupportedBanksUseCase,
    private val dispatchers: CoroutineDispatchers,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<BankSelectionState, BankSelectionIntent>(savedStateHandle) {

    override fun getInitialData() = BankSelectionState()

    private var banks: List<Bank> = emptyList()
        set(value) {
            field = value
            savedStateHandle.set("banks", value)
        }
        get() {
            return if (field.isNotEmpty()) {
                field
            } else {
                savedStateHandle.get("banks") ?: emptyList()
            }
        }

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
        viewModelScope.launch(dispatchers.io) {
            try {
                var disableCountrySelection = configuration.isCountrySelectionDisabled
                val supportedCountries = countryUseCase.getSupportedCountries(
                    configuration.authState,
                    configuration.countryFilter
                )
                var selectedCountry = supportedCountries.firstOrNull { it == configuration.selectedCountry }
                if (selectedCountry == null) {
                    disableCountrySelection = false
                    selectedCountry = supportedCountries.first()
                }
                val apiBanks = banksUseCase.getSupportedBanks(
                    selectedCountry,
                    configuration.authState,
                    SupportedBanksFilter(
                        banks = configuration.bankFilter,
                        isAccountLinking = configuration.isAccountLinking
                    )
                )

                banks = apiBanks.map {
                    Bank(it.id, it.name, it.officialName, it.imageUri, it.bic)
                }

                updateState {
                    it.copy(
                        selectedCountry = selectedCountry,
                        isCountrySelectionDisabled = disableCountrySelection,
                        loadingState = LoadingState.Loading(false),
                        bankListItems = BankListItemFactory.getBankList(apiBanks, configuration.selectedBankId)
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
        val config = CountrySelectionFragmentConfiguration(
            state.value.selectedCountry,
            configuration.countryFilter,
            configuration.authState,
            isAccountLinking = configuration.isAccountLinking
        )
        GlobalRouter.pushModalFragment(CountrySelectionContract.getFragment(config))
    }

    private suspend fun handleCountrySelected(
        selectedCountry: String,
        configuration: BankSelectionFragmentConfiguration
    ) {
        updateState {
            it.copy(
                loadingState = LoadingState.Loading(true)
            )
        }
        viewModelScope.launch(dispatchers.io) {
            try {
                val apiBanks = banksUseCase.getSupportedBanks(
                    selectedCountry,
                    configuration.authState,
                    SupportedBanksFilter(
                        banks = configuration.bankFilter,
                        isAccountLinking = configuration.isAccountLinking
                    )
                )
                banks = apiBanks.map {
                    Bank(it.id, it.name, it.officialName, it.imageUri, it.bic)
                }

                updateState {
                    it.copy(
                        selectedCountry = selectedCountry,
                        loadingState = LoadingState.Loading(false),
                        bankListItems = BankListItemFactory.getBankList(apiBanks)
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
            BankSelectionContract,
            banks.first { it.id == selectedBank.bankId }
        )
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(owner: SavedStateRegistryOwner) : AbstractSavedStateViewModelFactory(owner, null) {
        override fun <T : ViewModel?> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return BankSelectionViewModel(
                SupportedCountryUseCase(
                    KevinCountriesManager(AccountsClientProvider.kevinAccountsClient)
                ),
                GetSupportedBanksUseCase(
                    KevinBankManager(AccountsClientProvider.kevinAccountsClient)
                ),
                DefaultCoroutineDispatchers,
                handle
            ) as T
        }
    }
}