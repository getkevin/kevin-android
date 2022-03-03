package eu.kevin.inapppayments.paymentconfirmation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.viewModels
import eu.kevin.common.architecture.BaseFragment
import eu.kevin.common.architecture.interfaces.IView
import eu.kevin.common.extensions.getCurrentLocale
import eu.kevin.common.helpers.WebFrameHelper
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
        viewModel.intents.trySend(
            Initialize(
                configuration = configuration!!,
                webFrameQueryParameters = WebFrameHelper.getStyleAndLanguageQueryParameters(
                    context = requireContext()
                )
            )
        )
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

    override fun handleUri(uri: Uri) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        } catch (ignored: Exception) {
        }
    }
}