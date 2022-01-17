package eu.kevin.inapppayments.cardpayment

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import eu.kevin.common.architecture.BaseViewModel
import eu.kevin.inapppayments.BuildConfig
import eu.kevin.inapppayments.cardpayment.CardPaymentIntent.Initialize

internal class CardPaymentViewModel(
    savedStateHandle: SavedStateHandle
) : BaseViewModel<CardPaymentState, CardPaymentIntent>(savedStateHandle) {
    override fun getInitialData() = CardPaymentState()

    override suspend fun handleIntent(intent: CardPaymentIntent) {
        when (intent) {
            is Initialize -> initialize(intent.configuration)
        }
    }

    private suspend fun initialize(configuration: CardPaymentFragmentConfiguration) {
        val url = BuildConfig.KEVIN_CARD_PAYMENT_URL.format(configuration.paymentId)
        updateState {
            it.copy(url = url)
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(owner: SavedStateRegistryOwner) : AbstractSavedStateViewModelFactory(owner, null) {
        override fun <T : ViewModel?> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return CardPaymentViewModel(
                handle
            ) as T
        }
    }
}