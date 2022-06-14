package eu.kevin.demo.screens.paymenttype

import android.content.Context
import androidx.fragment.app.viewModels
import eu.kevin.common.architecture.BaseModalFragment
import eu.kevin.common.architecture.interfaces.IView
import eu.kevin.demo.screens.paymenttype.enums.DemoPaymentType

class PaymentTypeFragment : BaseModalFragment<PaymentTypeState, PaymentTypeIntent, PaymentTypeViewModel>(),
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
        viewModel.intents.trySend(PaymentTypeIntent.Initialize(configuration!!))
    }

    override fun onBankPaymentSelected() {
        dismiss()
        viewModel.intents.trySend(PaymentTypeIntent.OnPaymentTypeChosen(DemoPaymentType.BANK))
    }

    override fun onLinkedPaymentSelected() {
        dismiss()
        viewModel.intents.trySend(PaymentTypeIntent.OnPaymentTypeChosen(DemoPaymentType.LINKED))
    }

    override fun onCardPaymentSelected() {
        dismiss()
        viewModel.intents.trySend(PaymentTypeIntent.OnPaymentTypeChosen(DemoPaymentType.CARD))
    }
}