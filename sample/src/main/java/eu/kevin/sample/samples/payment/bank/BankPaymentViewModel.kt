package eu.kevin.sample.samples.payment.bank

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.kevin.core.entities.SessionResult
import eu.kevin.core.enums.KevinCountry
import eu.kevin.inapppayments.KevinPaymentsPlugin
import eu.kevin.inapppayments.paymentsession.PaymentSessionResult
import eu.kevin.sample.networking.KevinApiProvider
import eu.kevin.sample.networking.api.KevinApi
import eu.kevin.sample.networking.api.KevinDataApi
import eu.kevin.sample.networking.entities.payments.InitiatePaymentRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class BankPaymentViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BankPaymentUiState())
    val uiState: StateFlow<BankPaymentUiState> = _uiState.asStateFlow()

    private val kevinApi: KevinApi = KevinApiProvider.provideKevinApi()
    private val kevinDataApi: KevinDataApi = KevinApiProvider.provideKevinDataApi()

    /**
     * You can modify country to your own liking.
     */
    private val country = KevinCountry.LITHUANIA

    /**
     * We'll be starting with fetching demo charity creditors for our payment.
     */
    init {
        initiatePaymentCreditors()
    }

    private fun initiatePaymentCreditors() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val creditors = kevinDataApi.fetchCreditors(country)
                if (creditors.isEmpty()) {
                    // Inform user if there are no creditors available for specified country.
                    _uiState.update {
                        it.copy(userMessage = "No creditors available for ${country.name}")
                    }
                } else {
                    // Otherwise, pick first available creditor.
                    _uiState.update {
                        it.copy(
                            paymentCreditor = creditors.first(),
                            paymentCountry = country,
                            isLoading = false
                        )
                    }
                }
            } catch (error: Throwable) {
                _uiState.update {
                    it.copy(
                        userMessage = error.message ?: "Something went wrong",
                        isLoading = false
                    )
                }
            }
        }
    }

    /**
     * For initialising bank payment, you will need to obtain a paymentId.
     *
     * If you want your users to be able to bypass the authentication part of the bank payment process
     * then the account must be linked beforehand.
     *
     * More info:
     * https://api-reference.kevin.eu/public/platform/v0.3#tag/Payment-Initiation-Service/operation/initiatePayment
     */
    fun initiateBankPayment() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val creditor = _uiState.value.paymentCreditor
                if (creditor == null) {
                    _uiState.update { it.copy(userMessage = "No creditors available for ${country.name}") }
                    return@launch
                }
                val creditorAccount = creditor.accounts.first()

                val request = InitiatePaymentRequest(
                    amount = "0.01", // Some banks may refuse low amount transactions such as 0.01
                    email = "sample@sample.com",
                    creditorName = creditor.name,
                    iban = creditorAccount.iban,
                    currencyCode = creditorAccount.currencyCode,
                    redirectUrl = KevinPaymentsPlugin.getCallbackUrl()
                )
                val paymentId = kevinApi.initiateBankPayment(request)

                _uiState.update {
                    it.copy(
                        paymentId = paymentId,
                        isLoading = false
                    )
                }
            } catch (error: Throwable) {
                _uiState.update {
                    it.copy(
                        userMessage = error.message ?: "Something went wrong",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun handlePaymentInitiationResult(result: SessionResult<PaymentSessionResult>) {
        when (result) {
            is SessionResult.Success -> _uiState.update {
                it.copy(userMessage = "Success! Payment has been completed.")
            }

            is SessionResult.Failure -> _uiState.update {
                // Payment initiation session has failed.
                // Handle this case in your app accordingly.
                it.copy(userMessage = "Payment has failed! ${result.error.message}")
            }

            is SessionResult.Canceled -> _uiState.update {
                // Payment initiation session has been abandoned/cancelled.
                // Handle this case in your app accordingly.
                it.copy(userMessage = "Payment initiation has been cancelled.")
            }
        }
    }

    fun onPaymentInitiated() {
        _uiState.update { it.copy(paymentId = null) }
    }

    fun onUserMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }
}