package eu.kevin.demo.payment

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import eu.kevin.common.entities.LoadingState
import eu.kevin.core.networking.exceptions.ApiError
import eu.kevin.demo.ClientProvider
import eu.kevin.demo.auth.KevinApiClient
import eu.kevin.demo.auth.entities.InitiatePaymentRequest
import eu.kevin.demo.countryselection.CountrySelectionContract
import eu.kevin.demo.countryselection.CountrySelectionFragmentConfiguration
import eu.kevin.demo.extensions.toRepresentableBigDecimal
import eu.kevin.demo.payment.entities.CreditorListItem
import eu.kevin.demo.payment.entities.DonationRequest
import eu.kevin.demo.payment.entities.InitiateDonationRequest
import eu.kevin.demo.payment.entities.exceptions.CreditorNotSelectedException
import eu.kevin.demo.payment.factories.CreditorsListFactory
import eu.kevin.demo.payment.usecases.GetCreditorsUseCase
import eu.kevin.demo.payment.validation.AmountValidator
import eu.kevin.demo.payment.validation.EmailValidator
import eu.kevin.demo.routing.DemoRouter
import eu.kevin.inapppayments.paymentsession.enums.PaymentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class PaymentViewModel constructor(
    private val getCreditorsUseCase: GetCreditorsUseCase,
    private val kevinApiClient: KevinApiClient
) : ViewModel() {

    private val _viewState = MutableStateFlow(PaymentViewState())
    private val _viewAction = Channel<PaymentViewAction>(Channel.BUFFERED)

    val viewState: StateFlow<PaymentViewState> = _viewState
    val viewAction = _viewAction.receiveAsFlow()

    init {
        loadCreditors(_viewState.value.selectedCountry)
    }

    fun onCreditorSelected(creditor: CreditorListItem) {
        _viewState.update {
            it.copy(
                creditors = it.creditors.map {
                    it.copy(isSelected = it == creditor)
                },
                loadingState = LoadingState.Loading(false)
            )
        }
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

    fun openCountrySelection() {
        val config = CountrySelectionFragmentConfiguration(
            _viewState.value.selectedCountry
        )
        DemoRouter.pushModalFragment(CountrySelectionContract.getFragment(config))
    }

    fun onAmountChanged(amount: String) {
        _viewState.update {
            it.copy(
                buttonText = amount.toRepresentableBigDecimal() ?: "0.00",
                loadingState = LoadingState.Loading(false)
            )
        }
    }

    fun donate(donationRequest: DonationRequest) {
        val emailValidationResult = EmailValidator.validate(donationRequest.email.trim())
        val amountValidationResult = AmountValidator.validate(donationRequest.amount)

        _viewAction.trySend(
            PaymentViewAction.ShowFieldValidations(
                emailValidationResult,
                amountValidationResult,
                donationRequest.isTermsAccepted
            )
        )

        if (
            emailValidationResult.isValid() &&
            amountValidationResult.isValid() &&
            donationRequest.isTermsAccepted
        ) {
            val selectedCreditor = viewState.value.creditors.firstOrNull { it.isSelected }

            if (selectedCreditor == null) {
                _viewState.update {
                    it.copy(
                        loadingState = LoadingState.Failure(CreditorNotSelectedException())
                    )
                }
            } else {
                initiatePayment(
                    InitiateDonationRequest(
                        email = donationRequest.email.trim(),
                        amount = donationRequest.amount,
                        iban = selectedCreditor.iban,
                        creditorName = selectedCreditor.name,
                        paymentType = donationRequest.paymentType
                    )
                )
            }
        }
    }

    fun onPaymentSuccessful() {
        _viewState.update {
            PaymentViewState(
                creditors = it.creditors,
                selectedCountry = it.selectedCountry
            )
        }
        _viewAction.trySend(PaymentViewAction.ResetFields)
        _viewAction.trySend(PaymentViewAction.ShowSuccessDialog)
    }

    fun onPaymentFailure(error: Throwable) {
        _viewState.update { it.copy(loadingState = LoadingState.Failure(error)) }
    }

    private fun loadCreditors(countryIso: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.update {
                it.copy(loadingCreditors = true)
            }
            try {
                val creditors = CreditorsListFactory.getCreditorsList(
                    creditors = getCreditorsUseCase.getCreditors(countryIso)
                )
                _viewState.update {
                    it.copy(
                        creditors = creditors.mapIndexed { index, item ->
                            item.copy(isSelected = index == 0)
                        },
                        loadingCreditors = false
                    )
                }
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

    private fun initiatePayment(
        initiateDonationRequest: InitiateDonationRequest
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.update { it.copy(loadingState = LoadingState.Loading(true)) }
            try {
                val initiatePaymentRequest = InitiatePaymentRequest(
                    amount = initiateDonationRequest.amount,
                    email = initiateDonationRequest.email,
                    iban = initiateDonationRequest.iban,
                    creditorName = initiateDonationRequest.creditorName,
                    redirectUrl = "kevin://redirect.payment"
                )
                val payment = when (initiateDonationRequest.paymentType) {
                    PaymentType.BANK ->
                        kevinApiClient.initializeBankPayment(
                            initiatePaymentRequest
                        )
                    PaymentType.CARD ->
                        kevinApiClient.initializeCardPayment(
                            initiatePaymentRequest
                        )
                }
                _viewAction.send(
                    PaymentViewAction.OpenPaymentSession(
                        payment = payment,
                        paymentType = initiateDonationRequest.paymentType
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
            return PaymentViewModel(
                GetCreditorsUseCase(
                    ClientProvider.kevinApiClient
                ),
                ClientProvider.kevinDemoApiClient
            ) as T
        }
    }
}