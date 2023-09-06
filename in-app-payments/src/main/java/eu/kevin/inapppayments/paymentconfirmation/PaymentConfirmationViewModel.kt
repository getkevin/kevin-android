package eu.kevin.inapppayments.paymentconfirmation

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import eu.kevin.common.architecture.BaseViewModel
import eu.kevin.common.architecture.routing.GlobalRouter
import eu.kevin.common.extensions.appendQuery
import eu.kevin.common.fragment.FragmentResult
import eu.kevin.core.plugin.Kevin
import eu.kevin.inapppayments.BuildConfig
import eu.kevin.inapppayments.KevinPaymentsPlugin
import eu.kevin.inapppayments.enums.PaymentStatus
import eu.kevin.inapppayments.paymentconfirmation.PaymentConfirmationEvent.LoadWebPage
import eu.kevin.inapppayments.paymentconfirmation.PaymentConfirmationIntent.HandleBackClicked
import eu.kevin.inapppayments.paymentconfirmation.PaymentConfirmationIntent.HandlePaymentCompleted
import eu.kevin.inapppayments.paymentconfirmation.PaymentConfirmationIntent.Initialize
import eu.kevin.inapppayments.paymentsession.enums.PaymentType.BANK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

internal class PaymentConfirmationViewModel(
    savedStateHandle: SavedStateHandle
) : BaseViewModel<PaymentConfirmationState, PaymentConfirmationIntent>(savedStateHandle) {

    private val _events = Channel<PaymentConfirmationEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    override fun getInitialData() = PaymentConfirmationState()

    override suspend fun handleIntent(intent: PaymentConfirmationIntent) {
        when (intent) {
            is Initialize -> initialize(
                configuration = intent.configuration,
                webFrameQueryParameters = intent.webFrameQueryParameters
            )
            is HandleBackClicked -> GlobalRouter.popCurrentFragment()
            is HandlePaymentCompleted -> handlePaymentCompleted(intent.uri)
        }
    }

    private suspend fun initialize(
        configuration: PaymentConfirmationFragmentConfiguration,
        webFrameQueryParameters: String
    ) {
        val url = when (configuration.paymentType) {
            BANK -> {
                if (configuration.skipAuthentication) {
                    val baseAuthenticatedPaymentUrl = if (Kevin.isSandbox()) {
                        BuildConfig.KEVIN_SANDBOX_BANK_PAYMENT_AUTHENTICATED_URL
                    } else {
                        BuildConfig.KEVIN_BANK_PAYMENT_AUTHENTICATED_URL
                    }
                    baseAuthenticatedPaymentUrl.format(configuration.paymentId)
                        .appendQuery(webFrameQueryParameters)
                } else {
                    val basePaymentUrl = if (Kevin.isSandbox()) {
                        BuildConfig.KEVIN_SANDBOX_BANK_PAYMENT_URL
                    } else {
                        BuildConfig.KEVIN_BANK_PAYMENT_URL
                    }
                    basePaymentUrl.format(
                        configuration.paymentId,
                        configuration.selectedBank!!
                    ).appendQuery(webFrameQueryParameters)
                }
            }
        }

        updateState {
            it.copy(isProcessing = false)
        }

        initializeWebUrl(url)
    }

    private suspend fun initializeWebUrl(url: String) {
        val isDeepLinkingEnabled = Kevin.isDeepLinkingEnabled()

        /*
        We are checking for an existing redirect to avoid some
        possible extensive redirects after process death restoration.
         */
        if (isDeepLinkingEnabled && savedStateHandle.get<String>("redirect_url") == url) {
            updateState {
                it.copy(isProcessing = true)
            }
            return
        }

        if (isDeepLinkingEnabled) {
            savedStateHandle["redirect_url"] = url
        }

        _events.send(LoadWebPage(url))
    }

    private fun handlePaymentCompleted(uri: Uri) {
        if (!uri.toString().startsWith(KevinPaymentsPlugin.getCallbackUrl())) return

        when (val status = PaymentStatus.fromString(uri.getQueryParameter("statusGroup"))) {
            PaymentStatus.COMPLETED, PaymentStatus.PENDING -> {
                val result = PaymentConfirmationResult(
                    paymentId = uri.getQueryParameter("paymentId") ?: "",
                    status = status
                )
                GlobalRouter.returnFragmentResult(
                    PaymentConfirmationContract,
                    FragmentResult.Success(result)
                )
            }
            else -> {
                GlobalRouter.returnFragmentResult(
                    PaymentConfirmationContract,
                    FragmentResult.Failure(
                        error = Exception("Payment was canceled!")
                    )
                )
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PaymentConfirmationViewModel(
                    savedStateHandle = createSavedStateHandle()
                )
            }
        }
    }
}