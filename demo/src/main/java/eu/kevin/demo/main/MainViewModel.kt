package eu.kevin.demo.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.kevin.demo.BuildConfig
import eu.kevin.demo.auth.KevinApiClientFactory
import eu.kevin.demo.auth.entities.InitiateAuthenticationRequest
import eu.kevin.demo.auth.entities.InitiatePaymentRequest
import eu.kevin.demo.auth.enums.AuthenticationScope
import eu.kevin.inapppayments.paymentsession.enums.PaymentType
import io.ktor.client.features.logging.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val kevinAuthClient = KevinApiClientFactory(
        baseUrl = BuildConfig.KEVIN_API_URL,
        "",
        timeout = 120000,
        logLevel = LogLevel.NONE
    ).createClient(null)

    private val _viewState = MutableStateFlow<MainViewState>(MainViewState.Loading(false))
    private val _viewAction = Channel<MainViewAction>(Channel.BUFFERED)

    val viewState: StateFlow<MainViewState> = _viewState
    val viewAction = _viewAction.receiveAsFlow()

    fun initializeAccountLinking() {
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.update { MainViewState.Loading(true) }
            try {
                val state = kevinAuthClient.getAuthState(
                    InitiateAuthenticationRequest(
                        scopes = listOf(AuthenticationScope.PAYMENTS.value)
                    )
                )
                _viewAction.send(MainViewAction.OpenAccountLinkingSession(state))
                _viewState.update { MainViewState.Loading(false) }
            } catch (ignored: Exception) {
                _viewState.update { MainViewState.Loading(false) }
            }
        }
    }

    fun initializeBankPayment() {
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.update { MainViewState.Loading(true) }
            try {
                val payment = kevinAuthClient.initializeBankPayment(
                    InitiatePaymentRequest("0.01")
                )
                _viewAction.send(MainViewAction.OpenPaymentSession(payment, PaymentType.BANK))
                _viewState.update { MainViewState.Loading(false) }
            } catch (ignored: Exception) {
                _viewState.update { MainViewState.Loading(false) }
            }
        }
    }

    fun initializeCardPayment(isHybrid: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.update { MainViewState.Loading(true) }
            try {
                val payment = if (isHybrid) {
                    kevinAuthClient.initializeHybridPayment(
                        InitiatePaymentRequest("0.01")
                    )
                } else {
                    kevinAuthClient.initializeCardPayment(
                        InitiatePaymentRequest("0.01")
                    )
                }
                _viewAction.send(MainViewAction.OpenPaymentSession(payment, PaymentType.CARD))
                _viewState.update { MainViewState.Loading(false) }
            } catch (ignored: Exception) {
                _viewState.update { MainViewState.Loading(false) }
            }
        }
    }
}