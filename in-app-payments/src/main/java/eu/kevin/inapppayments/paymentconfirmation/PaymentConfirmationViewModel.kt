package eu.kevin.inapppayments.paymentconfirmation

import android.net.Uri
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import eu.kevin.common.architecture.BaseViewModel
import eu.kevin.common.architecture.routing.GlobalRouter
import eu.kevin.common.extensions.appendQuery
import eu.kevin.common.fragment.FragmentResult
import eu.kevin.core.plugin.Kevin
import eu.kevin.inapppayments.BuildConfig
import eu.kevin.inapppayments.KevinPaymentsPlugin
import eu.kevin.inapppayments.common.enums.PaymentStatus
import eu.kevin.inapppayments.paymentconfirmation.PaymentConfirmationIntent.HandleBackClicked
import eu.kevin.inapppayments.paymentconfirmation.PaymentConfirmationIntent.HandlePaymentCompleted
import eu.kevin.inapppayments.paymentconfirmation.PaymentConfirmationIntent.Initialize
import eu.kevin.inapppayments.paymentsession.enums.PaymentType.BANK

internal class PaymentConfirmationViewModel(
    savedStateHandle: SavedStateHandle
) : BaseViewModel<PaymentConfirmationState, PaymentConfirmationIntent>(savedStateHandle) {

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
            else -> {
                val baseCardPaymentUrl = if (Kevin.isSandbox()) {
                    BuildConfig.KEVIN_SANDBOX_CARD_PAYMENT_URL
                } else {
                    BuildConfig.KEVIN_CARD_PAYMENT_URL
                }
                baseCardPaymentUrl.format(configuration.paymentId)
                    .appendQuery(webFrameQueryParameters)
            }
        }
        updateState {
            it.copy(url = url)
        }
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
                GlobalRouter.returnFragmentResult(PaymentConfirmationContract, FragmentResult.Canceled)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        owner: SavedStateRegistryOwner
    ) : AbstractSavedStateViewModelFactory(owner, null) {
        override fun <T : ViewModel?> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return PaymentConfirmationViewModel(
                handle
            ) as T
        }
    }
}