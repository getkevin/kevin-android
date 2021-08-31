package eu.kevin.demo.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.kevin.demo.auth.KevinAuthClientFactory
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

    private val kevinAuthClient = KevinAuthClientFactory(
        baseUrl = "https://your.base.url/",
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
                val state = kevinAuthClient.getAuthState()
                _viewAction.send(MainViewAction.OpenAccountLinkingSession(state))
                _viewState.update { MainViewState.Loading(false) }
            } catch (ignored: Exception) {
                _viewState.update { MainViewState.Loading(false) }
            }
        }
    }

    fun initializePayment(paymentType: PaymentType) {
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.update { MainViewState.Loading(true) }
            try {
                val payment = if (paymentType == PaymentType.BANK) {
                    kevinAuthClient.initializeBankPayment()
                } else {
                    kevinAuthClient.initializeCardPayment()
                }
                _viewAction.send(MainViewAction.OpenPaymentSession(payment, paymentType))
                _viewState.update { MainViewState.Loading(false) }
            } catch (ignored: Exception) {
                _viewState.update { MainViewState.Loading(false) }
            }
        }
    }
}