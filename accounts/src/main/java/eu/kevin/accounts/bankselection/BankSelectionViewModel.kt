package eu.kevin.accounts.bankselection

import android.content.Context
import android.telephony.TelephonyManager
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
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
import eu.kevin.accounts.bankselection.providers.DefaultCountryIsoProvider
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
import eu.kevin.common.fragment.FragmentResult
import kotlinx.coroutines.launch

internal class BankSelectionViewModel constructor(
    private val defaultCountryIsoProvider: DefaultCountryIsoProvider,
    private val countryUseCase: SupportedCountryUseCase,
    private val banksUseCase: GetSupportedBanksUseCase,
    private val dispatchers: CoroutineDispatchers,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<BankSelectionState, BankSelectionIntent>(savedStateHandle) {

    override fun getInitialData() = BankSelectionState()

    private var banks: List<Bank> = emptyList()
        set(value) {
            field = value
            savedStateHandle["banks"] = value
        }
        get() {
            return field.ifEmpty {
                savedStateHandle["banks"] ?: emptyList()
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
                    val defaultCountry = defaultCountryIsoProvider.getDefaultCountryIso()
                    selectedCountry = supportedCountries
                        .firstOrNull { it.equals(defaultCountry, ignoreCase = true) }
                        ?: supportedCountries.first()
                }
                val apiBanks = banksUseCase.getSupportedBanks(
                    selectedCountry,
                    configuration.authState,
                    SupportedBanksFilter(
                        banks = configuration.bankFilter,
                        showOnlyAccountLinkingSupportedBanks = configuration.showOnlyAccountLinkingSupportedBanks
                    )
                )

                banks = apiBanks.map {
                    Bank(it.id, it.name, it.officialName, it.imageUri, it.bic)
                }

                val bankListItems = BankListItemFactory.getBankList(
                    apiBanks = apiBanks,
                    selectedBankId = configuration.selectedBankId
                )

                updateState {
                    it.copy(
                        selectedCountry = selectedCountry,
                        isCountrySelectionDisabled = disableCountrySelection,
                        isContinueVisible = bankListItems.any { item -> item.isSelected },
                        loadingState = LoadingState.Loading(false),
                        bankListItems = bankListItems
                    )
                }
            } catch (e: Exception) {
                GlobalRouter.returnFragmentResult(
                    BankSelectionContract,
                    FragmentResult.Failure(e)
                )
            }
        }
    }

    private suspend fun handleBankSelection(bankId: String) {
        updateState { oldState ->
            val bankListItems = state.value.bankListItems.map {
                it.copy(isSelected = it.bankId == bankId)
            }
            oldState.copy(
                bankListItems = bankListItems,
                isContinueVisible = bankListItems.any { it.isSelected }
            )
        }
    }

    private fun handleCountrySelectionClick(configuration: BankSelectionFragmentConfiguration) {
        val config = CountrySelectionFragmentConfiguration(
            state.value.selectedCountry,
            configuration.countryFilter,
            configuration.authState
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
                        showOnlyAccountLinkingSupportedBanks = configuration.showOnlyAccountLinkingSupportedBanks
                    )
                )
                banks = apiBanks.map {
                    Bank(it.id, it.name, it.officialName, it.imageUri, it.bic)
                }
                val bankListItems = BankListItemFactory.getBankList(apiBanks)

                updateState {
                    it.copy(
                        selectedCountry = selectedCountry,
                        loadingState = LoadingState.Loading(false),
                        bankListItems = bankListItems,
                        isContinueVisible = bankListItems.any { item -> item.isSelected }
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
            FragmentResult.Success(banks.first { it.id == selectedBank.bankId })
        )
    }

    companion object {
        fun createViewModelFactory(context: Context): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    BankSelectionViewModel(
                        savedStateHandle = createSavedStateHandle(),
                        defaultCountryIsoProvider = DefaultCountryIsoProvider(
                            context,
                            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                        ),
                        countryUseCase = SupportedCountryUseCase(
                            KevinCountriesManager(AccountsClientProvider.kevinAccountsClient)
                        ),
                        banksUseCase = GetSupportedBanksUseCase(
                            KevinBankManager(AccountsClientProvider.kevinAccountsClient)
                        ),
                        dispatchers = DefaultCoroutineDispatchers
                    )
                }
            }
        }
    }
}