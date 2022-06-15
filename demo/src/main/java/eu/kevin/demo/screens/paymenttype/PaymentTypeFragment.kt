package eu.kevin.demo.screens.paymenttype

import android.content.Context
import androidx.fragment.app.viewModels
import eu.kevin.common.architecture.BaseModalFragment
import eu.kevin.common.architecture.interfaces.IView
import eu.kevin.demo.screens.paymenttype.PaymentTypeIntent.Initialize
import eu.kevin.demo.screens.paymenttype.PaymentTypeIntent.OnPaymentTypeChosen
import eu.kevin.demo.screens.paymenttype.enums.DemoPaymentType

internal class PaymentTypeFragment :
    BaseModalFragment<PaymentTypeState, PaymentTypeIntent, PaymentTypeViewModel>(),
    PaymentTypeViewCallback {

    var configuration: PaymentTypeFragmentConfiguration? by savedState()

    override val viewModel by viewModels<PaymentTypeViewModel> {
        PaymentTypeViewModel.Factory(this)
    }

    override fun onCreateView(context: Context): IView<PaymentTypeState> {
        return PaymentTypeView(context).also {
            it.callback = this
        }
    }

    override fun onAttached() {
        super.onAttached()
        viewModel.intents.trySend(Initialize(configuration!!))
    }

    override fun onBankPaymentSelected() {
        dismiss()
        viewModel.intents.trySend(OnPaymentTypeChosen(DemoPaymentType.BANK))
    }

    override fun onLinkedPaymentSelected() {
        dismiss()
        viewModel.intents.trySend(OnPaymentTypeChosen(DemoPaymentType.LINKED))
    }

    override fun onCardPaymentSelected() {
        dismiss()
        viewModel.intents.trySend(OnPaymentTypeChosen(DemoPaymentType.CARD))
    }
}