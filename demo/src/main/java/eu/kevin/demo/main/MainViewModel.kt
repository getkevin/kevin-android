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
import eu.kevin.demo.extensions.toRepresentableBigDecimal
import eu.kevin.demo.main.entities.CreditorListItem
import eu.kevin.demo.main.entities.DonationConfiguration
import eu.kevin.demo.main.entities.toListItems
import eu.kevin.demo.main.usecases.GetCreditorsUseCase
import eu.kevin.demo.main.validation.AmountValidator
import eu.kevin.demo.main.validation.EmailValidator
import eu.kevin.inapppayments.paymentsession.enums.PaymentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class MainViewModel constructor(
    private val getCreditorsUseCase: GetCreditorsUseCase
) : ViewModel() {

    private val _viewState = MutableStateFlow(MainViewState())

    private val _viewAction = Channel<MainViewAction>(Channel.BUFFERED)

    private val donationConfiguration = DonationConfiguration()

    val viewState: StateFlow<MainViewState> = _viewState
    val viewAction = _viewAction.receiveAsFlow()

    init {
        loadCreditors(_viewState.value.selectedCountry)
    }

    private fun loadCreditors(countryIso: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.update {
                it.copy(loadingCreditors = true)
            }
            try {
                val creditors =
                    getCreditorsUseCase.getCreditors(countryIso).toListItems()
                _viewState.update {
                    it.copy(
                        creditors = creditors.mapIndexed { index, item ->
                            item.copy(isSelected = index == 0)
                        },
                        loadingCreditors = false
                    )
                }
                donationConfiguration.selectedCreditor = creditors.firstOrNull()
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

    fun onAmountChanged(amount: String) {
        _viewState.update {
            it.copy(
                buttonText = amount.toRepresentableBigDecimal(),
                loadingState = LoadingState.Loading(false)
            )
        }
    }

    fun onProceedClick(
        email: String,
        amount: String,
        isTermsAccepted: Boolean
    ) {
        val emailValidationResult = EmailValidator.validate(email)
        val amountValidationResult = AmountValidator.validate(amount)

        _viewAction.trySend(
            MainViewAction.ShowFieldValidations(
                emailValidationResult,
                amountValidationResult,
                isTermsAccepted
            )
        )

        if (emailValidationResult.isValid() && amountValidationResult.isValid() && isTermsAccepted) {
            initiatePayment(
                email = email,
                amount = amount.toRepresentableBigDecimal(),
                iban = donationConfiguration.selectedCreditor?.iban ?: "",
                creditorName = donationConfiguration.selectedCreditor?.name ?: ""
            )
        }
    }

    private fun initiatePayment(
        email: String,
        amount: String,
        iban: String,
        creditorName: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.update { it.copy(loadingState = LoadingState.Loading(true)) }
            try {
                val initiatePaymentRequest = InitiatePaymentRequest(
                    amount = amount,
                    email = email,
                    iban = iban,
                    creditorName = creditorName
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