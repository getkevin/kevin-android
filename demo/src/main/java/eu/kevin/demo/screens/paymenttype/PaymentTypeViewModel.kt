package eu.kevin.demo.screens.paymenttype

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import eu.kevin.common.architecture.BaseViewModel
import eu.kevin.demo.routing.DemoRouter
import eu.kevin.demo.screens.paymenttype.PaymentTypeIntent.Initialize
import eu.kevin.demo.screens.paymenttype.PaymentTypeIntent.OnPaymentTypeChosen

internal class PaymentTypeViewModel(
    savedStateHandle: SavedStateHandle
) : BaseViewModel<PaymentTypeState, PaymentTypeIntent, Nothing>(savedStateHandle) {

    override fun getInitialData() = PaymentTypeState()

    override suspend fun handleIntent(intent: PaymentTypeIntent) {
        when (intent) {
            is OnPaymentTypeChosen -> {
                DemoRouter.returnFragmentResult(PaymentTypeContract, intent.demoPaymentType)
            }
            is Initialize -> {
                updateState {
                    it.copy(showLinkedAccountOption = intent.paymentTypeFragmentConfiguration.linkedPaymentAvailable)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        owner: SavedStateRegistryOwner
    ) :
        AbstractSavedStateViewModelFactory(owner, null) {
        override fun <T : ViewModel?> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return PaymentTypeViewModel(
                handle
            ) as T
        }
    }
}