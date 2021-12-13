package eu.kevin.inapppayments.paymentconfirmation

import android.content.Context
import android.net.Uri
import androidx.fragment.app.viewModels
import eu.kevin.common.architecture.BaseFragment
import eu.kevin.common.architecture.interfaces.IView
import eu.kevin.inapppayments.paymentconfirmation.PaymentConfirmationIntent.*

internal class PaymentConfirmationFragment : BaseFragment<PaymentConfirmationState, PaymentConfirmationIntent, PaymentConfirmationViewModel>(),
    PaymentConfirmationViewDelegate {

    override val viewModel: PaymentConfirmationViewModel by viewModels {
        PaymentConfirmationViewModel.Factory(this)
    }

    var configuration: PaymentConfirmationFragmentConfiguration? by savedState()

    private lateinit var view: PaymentConfirmationView

    override fun onCreateView(context: Context): IView<PaymentConfirmationState> {
        return PaymentConfirmationView(context).also {
            it.delegate = this
            view = it
        }
    }

    override fun onAttached() {
        super.onAttached()
        viewModel.intents.trySend(Initialize(configuration!!))
    }

    override fun onBackPressed(): Boolean {
        if (!view.handleWebViewBackPress()) {
            viewModel.intents.trySend(HandleBackClicked)
        }
        return true
    }

    // PaymentConfirmationViewDelegate

    override fun onBackClicked() {
        if (!view.handleWebViewBackPress()) {
            viewModel.intents.trySend(HandleBackClicked)
        }
    }

    override fun onPaymentCompleted(uri: Uri) {
        viewModel.intents.trySend(HandlePaymentCompleted(uri))
    }
}