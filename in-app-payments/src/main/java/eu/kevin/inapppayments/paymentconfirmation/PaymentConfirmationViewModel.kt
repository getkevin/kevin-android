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
    savedStateHandle: SavedStateHandle
) : BaseViewModel<PaymentConfirmationState, PaymentConfirmationIntent>(savedStateHandle) {

    override fun getInitialData() = PaymentConfirmationState()

    override suspend fun handleIntent(intent: PaymentConfirmationIntent) {
        when (intent) {
            is Initialize -> initialize(
                configuration = intent.configuration,
                defaultLocale = intent.defaultLocale,
                paymentConfirmationFrameColorsConfiguration = intent.kevinFrameColorsConfiguration
            )
            is HandleBackClicked -> GlobalRouter.popCurrentFragment()
            is HandlePaymentCompleted -> handlePaymentCompleted(intent.uri)
        }
    }

    private suspend fun initialize(
        configuration: PaymentConfirmationFragmentConfiguration,
        paymentConfirmationFrameColorsConfiguration: PaymentConfirmationFrameColorsConfiguration,
        defaultLocale: Locale
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
                        url = baseAuthenticatedPaymentUrl.format(configuration.paymentId),
                        paymentConfirmationFrameColorsConfiguration = paymentConfirmationFrameColorsConfiguration,
                        deviceLocale = defaultLocale
                    )
                } else {
                    val basePaymentUrl = if (Kevin.isSandbox()) {
                        BuildConfig.KEVIN_SANDBOX_BANK_PAYMENT_URL
                    } else {
                        BuildConfig.KEVIN_BANK_PAYMENT_URL
                    }
                    appendRequiredQueryParameters(
                        url = basePaymentUrl.format(
                            configuration.paymentId,
                            configuration.selectedBank!!
                        ),
                        paymentConfirmationFrameColorsConfiguration = paymentConfirmationFrameColorsConfiguration,
                        deviceLocale = defaultLocale
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
                    url = baseCardPaymentUrl.format(configuration.paymentId),
                    paymentConfirmationFrameColorsConfiguration = paymentConfirmationFrameColorsConfiguration,
                    deviceLocale = defaultLocale
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
        paymentConfirmationFrameColorsConfiguration: PaymentConfirmationFrameColorsConfiguration,
        deviceLocale: Locale
    ): String {
        return url
            .appendQueryParameter("lang", getActiveLocaleCode(deviceLocale))
            .appendQueryParameter(
                "cs",
                Json.encodeToString(paymentConfirmationFrameColorsConfiguration)
            )
    }

    private fun getActiveLocaleCode(defaultLocale: Locale): String {
        return Kevin.getLocale()?.language ?: defaultLocale.language
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