package eu.kevin.inapppayments.paymentconfirmation

import android.net.Uri
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import eu.kevin.common.architecture.BaseViewModel
import eu.kevin.common.architecture.routing.GlobalRouter
import eu.kevin.common.fragment.FragmentResult
import eu.kevin.core.plugin.Kevin
import eu.kevin.inapppayments.BuildConfig
import eu.kevin.inapppayments.paymentconfirmation.PaymentConfirmationIntent.*
import eu.kevin.inapppayments.paymentconfirmation.entities.PaymentConfirmationFrameColorsConfiguration
import eu.kevin.inapppayments.paymentconfirmation.helpers.appendQueryParameter
import eu.kevin.inapppayments.paymentsession.enums.PaymentType.BANK
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

internal class PaymentConfirmationViewModel(
    savedStateHandle: SavedStateHandle,
    private val deviceLocale: Locale
) : BaseViewModel<PaymentConfirmationState, PaymentConfirmationIntent>(savedStateHandle) {

    override fun getInitialData() = PaymentConfirmationState()

    override suspend fun handleIntent(intent: PaymentConfirmationIntent) {
        when (intent) {
            is Initialize -> initialize(intent.configuration, intent.kevinFrameColorsConfiguration)
            is HandleBackClicked -> GlobalRouter.popCurrentFragment()
            is HandlePaymentCompleted -> handlePaymentCompleted(intent.uri)
        }
    }

    private suspend fun initialize(
        configuration: PaymentConfirmationFragmentConfiguration,
        paymentConfirmationFrameColorsConfiguration: PaymentConfirmationFrameColorsConfiguration
    ) {
        val url = when (configuration.paymentType) {
            BANK -> {
                if (configuration.skipAuthentication) {
                    val baseAuthenticatedPaymentUrl = if (Kevin.isSandbox()) {
                        BuildConfig.KEVIN_SANDBOX_BANK_PAYMENT_AUTHENTICATED_URL
                    } else {
                        BuildConfig.KEVIN_BANK_PAYMENT_AUTHENTICATED_URL
                    }
                    appendRequiredQueryParameters(
                        baseAuthenticatedPaymentUrl.format(configuration.paymentId),
                        paymentConfirmationFrameColorsConfiguration
                    )
                } else {
                    val basePaymentUrl = if (Kevin.isSandbox()) {
                        BuildConfig.KEVIN_SANDBOX_BANK_PAYMENT_URL
                    } else {
                        BuildConfig.KEVIN_BANK_PAYMENT_URL
                    }
                    appendRequiredQueryParameters(
                        basePaymentUrl.format(
                            configuration.paymentId,
                            configuration.selectedBank!!
                        ),
                        paymentConfirmationFrameColorsConfiguration
                    )
                }
            }
            else -> {
                val baseCardPaymentUrl = if (Kevin.isSandbox()) {
                    BuildConfig.KEVIN_SANDBOX_CARD_PAYMENT_URL
                } else {
                    BuildConfig.KEVIN_CARD_PAYMENT_URL
                }
                appendRequiredQueryParameters(
                    baseCardPaymentUrl.format(configuration.paymentId),
                    paymentConfirmationFrameColorsConfiguration
                )
            }
        }
        updateState {
            it.copy(url = url)
        }
    }

    private fun handlePaymentCompleted(uri: Uri) {
        val status = uri.getQueryParameter("statusGroup")
        if (status == "completed") {
            val result = PaymentConfirmationResult(
                uri.getQueryParameter("paymentId") ?: ""
            )
            GlobalRouter.returnFragmentResult(
                PaymentConfirmationContract,
                FragmentResult.Success(result)
            )
        } else {
            GlobalRouter.returnFragmentResult(PaymentConfirmationContract, FragmentResult.Canceled)
        }
    }

    private fun appendRequiredQueryParameters(
        url: String,
        paymentConfirmationFrameColorsConfiguration: PaymentConfirmationFrameColorsConfiguration
    ): String {
        return url
            .appendQueryParameter("lang", getKevinPluginLanguage())
            .appendQueryParameter(
                "cs",
                Json.encodeToString(paymentConfirmationFrameColorsConfiguration)
            )
    }

    private fun getKevinPluginLanguage(): String {
        return Kevin.getLocale()?.language ?: deviceLocale.language
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        owner: SavedStateRegistryOwner,
        private val deviceLocale: Locale
    ) : AbstractSavedStateViewModelFactory(owner, null) {
        override fun <T : ViewModel?> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return PaymentConfirmationViewModel(
                handle,
                deviceLocale
            ) as T
        }
    }
}