package eu.kevin.demo.screens.payment

import android.content.Context
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import eu.kevin.common.entities.LoadingState
import eu.kevin.core.enums.KevinCountry
import eu.kevin.core.networking.exceptions.ApiError
import eu.kevin.demo.data.ClientProvider
import eu.kevin.demo.auth.KevinApiClient
import eu.kevin.demo.auth.entities.InitiatePaymentRequest
import eu.kevin.demo.data.database.DatabaseProvider
import eu.kevin.demo.data.database.LinkedAccountsDao
import eu.kevin.demo.extensions.toRepresentableBigDecimal
import eu.kevin.demo.preferences.AccountAccessTokenPreferences
import eu.kevin.demo.routing.DemoRouter
import eu.kevin.demo.screens.chooseaccount.ChooseAccountContract
import eu.kevin.demo.screens.countryselection.CountrySelectionContract
import eu.kevin.demo.screens.countryselection.CountrySelectionFragmentConfiguration
import eu.kevin.demo.screens.payment.entities.CreditorListItem
import eu.kevin.demo.screens.payment.entities.DonationRequest
import eu.kevin.demo.screens.payment.entities.InitiateDonationRequest
import eu.kevin.demo.screens.payment.entities.exceptions.CreditorNotSelectedException
import eu.kevin.demo.screens.payment.factories.CreditorsListFactory
import eu.kevin.demo.screens.payment.usecases.GetCreditorsUseCase
import eu.kevin.demo.screens.payment.validation.AmountValidator
import eu.kevin.demo.screens.payment.validation.EmailValidator
import eu.kevin.demo.screens.paymenttype.PaymentTypeContract
import eu.kevin.demo.screens.paymenttype.PaymentTypeFragmentConfiguration
import eu.kevin.demo.screens.paymenttype.enums.DemoPaymentType
import eu.kevin.demo.usecases.GetAccessTokenForAccountUseCase
import eu.kevin.demo.usecases.InitialiseLinkedPaymentUseCase
import eu.kevin.demo.usecases.RefreshAccessTokenUseCase
import eu.kevin.inapppayments.KevinPaymentsPlugin
import eu.kevin.inapppayments.paymentsession.entities.PaymentSessionConfiguration
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
    private val kevinApiClient: KevinApiClient,
    private val linkedAccountsDao: LinkedAccountsDao,
    private val initialiseLinkedPaymentUseCase: InitialiseLinkedPaymentUseCase
) : ViewModel() {

    private val _viewState = MutableStateFlow(PaymentViewState())
    private val _viewAction = Channel<PaymentViewAction>(Channel.BUFFERED)

    val viewState: StateFlow<PaymentViewState> = _viewState
    val viewAction = _viewAction.receiveAsFlow()

    private var donationRequest: DonationRequest? = null

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

    fun onPaymentTypeSelected(demoPaymentType: DemoPaymentType) {
        val selectedCreditor = viewState.value.creditors.firstOrNull { it.isSelected }!!

        when (demoPaymentType) {
            DemoPaymentType.LINKED -> {
                DemoRouter.pushModalFragment(ChooseAccountContract.getFragment())
            }
            else -> {
                initiateSinglePayment(
                    InitiateDonationRequest(
                        email = donationRequest!!.email.trim(),
                        amount = donationRequest!!.amount,
                        iban = selectedCreditor.iban,
                        creditorName = selectedCreditor.name,
                        demoPaymentType = demoPaymentType
                    ),
                    demoPaymentType = demoPaymentType
                )
            }
        }
    }

    fun onAccountSelected(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.update { it.copy(loadingState = LoadingState.Loading(true)) }
            try {
                val selectedCreditor = viewState.value.creditors.firstOrNull { it.isSelected }!!

                val payment = initialiseLinkedPaymentUseCase.initialiseLinkedPayment(
                    InitiatePaymentRequest(
                        email = donationRequest!!.email.trim(),
                        amount = donationRequest!!.amount,
                        iban = selectedCreditor.iban,
                        creditorName = selectedCreditor.name,
                        redirectUrl = "kevin://redirect.payment"
                    ),
                    accountId = id
                )
                _viewAction.send(
                    PaymentViewAction.OpenPaymentSession(
                        PaymentSessionConfiguration.Builder(payment.id)
                            .setPaymentType(PaymentType.BANK)
                            .setSkipAuthentication(true)
                            .setPreselectedCountry(KevinCountry.LITHUANIA)
                            .build()
                    )
                )
                _viewState.update { it.copy(loadingState = LoadingState.Loading(false)) }
            } catch (error: ApiError) {
                _viewState.update { it.copy(loadingState = LoadingState.Failure(error)) }
            }
        }
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

    fun onDoneClick(donationRequest: DonationRequest) {
        this.donationRequest = donationRequest

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
                viewModelScope.launch(Dispatchers.IO) {
                    DemoRouter.pushModalFragment(
                        PaymentTypeContract.getFragment(
                            PaymentTypeFragmentConfiguration(
                                linkedPaymentAvailable = linkedAccountsDao.getLinkedAccounts().isNotEmpty()
                            )
                        )
                    )
                }
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

    private fun initiateSinglePayment(
        initiateDonationRequest: InitiateDonationRequest,
        demoPaymentType: DemoPaymentType
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.update { it.copy(loadingState = LoadingState.Loading(true)) }
            try {
                val initiatePaymentRequest = InitiatePaymentRequest(
                    amount = initiateDonationRequest.amount,
                    email = initiateDonationRequest.email,
                    iban = initiateDonationRequest.iban,
                    creditorName = initiateDonationRequest.creditorName,
                    redirectUrl = KevinPaymentsPlugin.getCallbackUrl()
                )
                val payment = when (demoPaymentType.toSdkPaymentType()) {
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
                        PaymentSessionConfiguration.Builder(payment.id)
                            .setPaymentType(demoPaymentType.toSdkPaymentType())
                            .setSkipAuthentication(false)
                            .setPreselectedCountry(KevinCountry.LITHUANIA)
                            .build()
                    )
                )
                _viewState.update { it.copy(loadingState = LoadingState.Loading(false)) }
            } catch (error: ApiError) {
                _viewState.update { it.copy(loadingState = LoadingState.Failure(error)) }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val context: Context,
        owner: SavedStateRegistryOwner
    ) :
        AbstractSavedStateViewModelFactory(owner, null) {
        override fun <T : ViewModel?> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            val linkedAccountsDao = DatabaseProvider.getDatabase(context).linkedAccountsDao()
            val refreshAccessTokenUseCase = RefreshAccessTokenUseCase(
                ClientProvider.kevinDemoApiClient,
                accountAccessTokenPreferences = AccountAccessTokenPreferences(context)
            )

            return PaymentViewModel(
                GetCreditorsUseCase(
                    ClientProvider.kevinApiClient
                ),
                ClientProvider.kevinDemoApiClient,
                linkedAccountsDao = linkedAccountsDao,
                initialiseLinkedPaymentUseCase = InitialiseLinkedPaymentUseCase(
                    ClientProvider.kevinDemoApiClient,
                    getAccessTokenForAccountUseCase = GetAccessTokenForAccountUseCase(
                        accountAccessTokenPreferences = AccountAccessTokenPreferences(context),
                        refreshAccessTokenUseCase = refreshAccessTokenUseCase
                    ),
                    refreshAccessTokenUseCase = refreshAccessTokenUseCase,
                    linkedAccountsDao = linkedAccountsDao
                )
            ) as T
        }
    }
}