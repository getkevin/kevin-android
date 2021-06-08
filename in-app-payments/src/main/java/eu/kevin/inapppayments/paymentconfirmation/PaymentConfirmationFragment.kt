package eu.kevin.inapppayments.paymentconfirmation

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.viewModels
import eu.kevin.core.architecture.BaseFragment
import eu.kevin.core.architecture.interfaces.IView
import eu.kevin.core.fragment.FragmentResultContract
import eu.kevin.inapppayments.paymentconfirmation.PaymentConfirmationIntent.*

internal class PaymentConfirmationFragment : BaseFragment<PaymentConfirmationState, PaymentConfirmationIntent, PaymentConfirmationViewModel>(),
    PaymentConfirmationViewDelegate {

    override val viewModel: PaymentConfirmationViewModel by viewModels {
        PaymentConfirmationViewModel.Factory(this)
    }

    var configuration: PaymentConfirmationFragmentConfiguration? by savedState()

    override fun onCreateView(context: Context): IView<PaymentConfirmationState> {
        return PaymentConfirmationView(context).also {
            it.delegate = this
        }
    }

    override fun onAttached() {
        super.onAttached()
        viewModel.intents.offer(Initialize(configuration!!))
    }

    override fun onBackPressed(): Boolean {
        viewModel.intents.offer(HandleBackClicked)
        return true
    }

    // PaymentConfirmationViewDelegate

    override fun onBackClicked() {
        viewModel.intents.offer(HandleBackClicked)
    }

    override fun onPaymentCompleted(uri: Uri) {
        viewModel.intents.offer(HandlePaymentCompleted(uri))
    }

    object Contract: FragmentResultContract<Unit>() {
        override val requestKey = "payment_confirmation_request_key"
        override val resultKey = "payment_confirmation_result_key"
        override fun parseResult(data: Bundle) {
            return
        }
    }
}