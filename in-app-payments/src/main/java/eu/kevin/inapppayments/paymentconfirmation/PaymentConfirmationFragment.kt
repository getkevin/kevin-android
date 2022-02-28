package eu.kevin.inapppayments.paymentconfirmation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import eu.kevin.common.architecture.BaseFragment
import eu.kevin.common.architecture.interfaces.IView
import eu.kevin.common.extensions.getColorFromAttr
import eu.kevin.common.extensions.getCurrentLocale
import eu.kevin.common.extensions.isDarkMode
import eu.kevin.common.extensions.toHexColor
import eu.kevin.inapppayments.R
import eu.kevin.inapppayments.paymentconfirmation.PaymentConfirmationIntent.*
import eu.kevin.inapppayments.paymentconfirmation.entities.PaymentConfirmationFrameColorsConfiguration

internal class PaymentConfirmationFragment :
    BaseFragment<PaymentConfirmationState, PaymentConfirmationIntent, PaymentConfirmationViewModel>(),
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
                defaultLocale = requireContext().getCurrentLocale(),
                kevinFrameColorsConfiguration = getKevinFrameColorsConfigurationFromTheme()
            )
        )
    }

    override fun onBackPressed(): Boolean {
        if (!view.handleWebViewBackPress()) {
            viewModel.intents.trySend(HandleBackClicked)
        }
        return true
    }

    private fun getKevinFrameColorsConfigurationFromTheme() =
        with(requireContext()) {
            PaymentConfirmationFrameColorsConfiguration(
                backgroundColor = getColorFromAttr(R.attr.kevinPrimaryBackgroundColor).toHexColor(),
                baseColor = getColorFromAttr(R.attr.kevinPrimaryBackgroundColor).toHexColor(),
                headingsColor = getColorFromAttr(R.attr.kevinPrimaryTextColor).toHexColor(),
                fontColor = getColorFromAttr(R.attr.kevinPrimaryTextColor).toHexColor(),
                bankIconColor = if (isDarkMode()) "white" else "default",
                defaultButtonColor = ContextCompat.getColor(this, R.color.kevin_blue).toHexColor()
            )
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