package eu.kevin.inapppayments.paymentconfirmation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import eu.kevin.common.architecture.BaseFragment
import eu.kevin.common.architecture.interfaces.DeepLinkHandler
import eu.kevin.common.architecture.interfaces.IView
import eu.kevin.common.extensions.launchOnRepeat
import eu.kevin.common.helpers.IntentHandlerHelper
import eu.kevin.common.helpers.WebFrameHelper
import eu.kevin.core.plugin.Kevin
import eu.kevin.inapppayments.paymentconfirmation.PaymentConfirmationIntent.HandleBackClicked
import eu.kevin.inapppayments.paymentconfirmation.PaymentConfirmationIntent.HandlePaymentCompleted
import eu.kevin.inapppayments.paymentconfirmation.PaymentConfirmationIntent.Initialize

internal class PaymentConfirmationFragment :
    BaseFragment<PaymentConfirmationState, PaymentConfirmationIntent, PaymentConfirmationViewModel>(),
    PaymentConfirmationViewDelegate,
    DeepLinkHandler {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        launchOnRepeat {
            viewModel.events.collect { this@PaymentConfirmationFragment.view.handleEvent(it) }
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

    override fun handleDeepLink(uri: Uri) {
        viewModel.intents.trySend(HandlePaymentCompleted(uri))
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

    override fun openAppIfAvailable(uri: Uri): Boolean {
        if (!Kevin.isDeepLinkingEnabled()) return false
        try {
            val intent = IntentHandlerHelper.getIntentForUri(requireContext(), uri) ?: return false
            startActivity(intent)
            return true
        } catch (ignored: Exception) {
            return false
        }
    }
}