package eu.kevin.demo.main

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import eu.kevin.common.architecture.routing.GlobalRouter
import eu.kevin.common.entities.LoadingState
import eu.kevin.core.networking.exceptions.ApiError
import eu.kevin.demo.ClientProvider
import eu.kevin.demo.auth.entities.InitiatePaymentRequest
import eu.kevin.demo.countryselection.CountrySelectionContract
import eu.kevin.demo.countryselection.CountrySelectionFragmentConfiguration
import eu.kevin.demo.main.entities.CreditorListItem
import eu.kevin.demo.main.entities.DonationConfiguration
import eu.kevin.demo.main.entities.toListItems
import eu.kevin.demo.main.usecases.GetCreditorsUseCase
import eu.kevin.inapppayments.paymentsession.enums.PaymentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel constructor(
    private val getCreditorsUseCase: GetCreditorsUseCase
) : ViewModel() {

    private val _viewState = MutableStateFlow(MainViewState())
    private val _viewAction = Channel<MainViewAction>(Channel.BUFFERED)

    private val donationConfiguration = DonationConfiguration()

    val viewState: StateFlow<MainViewState> = _viewState
    val viewAction = _viewAction.receiveAsFlow()

    init {
        loadCreditors(_viewState.value.selectedCountry)
        updateButtonState()
    }

    private fun loadCreditors(countryIso: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.update {
                it.copy(loadingCreditors = true)
            }
            try {
                val creditors =
                    getCreditorsUseCase.getCreditors(countryIso)
                _viewState.update {
                    it.copy(
                        creditors = creditors.toListItems(),
                        loadingCreditors = false
                    )
                }
                donationConfiguration.selectedCreditor = null
                updateButtonState()
            } catch (e: Exception) {
                _viewState.update {
                    it.copy(
                        loadingCreditors = false,
                        loadingState = LoadingState.Failure(e)
                    )
                }
            }
        }
    }

    fun onEmailChanged(email: String) {
        donationConfiguration.email = email
        updateButtonState()
    }

    fun onAmountChanged(amount: String) {
        donationConfiguration.amount = amount
        updateButtonState()
    }

    fun onTermsCheckBoxChanged(checked: Boolean) {
        donationConfiguration.termsAgreed = checked
        updateButtonState()
    }

    fun onCreditorSelected(creditor: CreditorListItem) {
        donationConfiguration.selectedCreditor = creditor
        _viewState.update {
            it.copy(
                creditors = it.creditors.map {
                    it.copy(isSelected = it == donationConfiguration.selectedCreditor)
                },
                loadingState = LoadingState.Loading(false)
            )
        }
        updateButtonState()
    }

    fun onPaymentTypeChanged(position: Int) {
        donationConfiguration.paymentType = PaymentType.values()[position]
    }

    fun onCountrySelected(iso: String) {
        _viewState.update {
            it.copy(
                selectedCountry = iso,
                loadingState = LoadingState.Loading(false)
            )
        }
        loadCreditors(iso)
    }

    fun onSelectCountryClick() {
        val config = CountrySelectionFragmentConfiguration(
            _viewState.value.selectedCountry
        )
        GlobalRouter.pushModalFragment(CountrySelectionContract.getFragment(config))
    }

    private fun updateButtonState() {
        _viewState.update {
            it.copy(
                proceedButtonEnabled = donationConfiguration.canProceed(),
                buttonText = donationConfiguration.getAmountText(),
                loadingState = LoadingState.Loading(false)
            )
        }
    }

    fun onProceedClick() {
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.update { it.copy(loadingState = LoadingState.Loading(true)) }
            try {
                val initiatePaymentRequest = InitiatePaymentRequest(
                    amount = donationConfiguration.getAmountText(),
                    email = donationConfiguration.email,
                    iban = donationConfiguration.selectedCreditor?.iban ?: "",
                    creditorName = donationConfiguration.selectedCreditor?.name ?: ""
                )
                val payment = when (donationConfiguration.paymentType) {
                    PaymentType.BANK ->
                        ClientProvider.kevinAuthClient.initializeBankPayment(
                            initiatePaymentRequest
                        )
                    PaymentType.CARD ->
                        ClientProvider.kevinAuthClient.initializeCardPayment(
                            initiatePaymentRequest
                        )
                }
                _viewAction.send(
                    MainViewAction.OpenPaymentSession(
                        payment,
                        donationConfiguration.paymentType
                    )
                )
                _viewState.update { it.copy(loadingState = LoadingState.Loading(false)) }
            } catch (error: ApiError) {
                _viewState.update { it.copy(loadingState = LoadingState.Failure(error)) }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(owner: SavedStateRegistryOwner) :
        AbstractSavedStateViewModelFactory(owner, null) {
        override fun <T : ViewModel?> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return MainViewModel(
                GetCreditorsUseCase(
                    ClientProvider.kevinDataClient
                )
            ) as T
        }
    }
}