package eu.kevin.inapppayments.paymentconfirmation

import android.content.Context
import android.view.LayoutInflater
import android.webkit.*
import eu.kevin.core.architecture.BaseView
import eu.kevin.core.architecture.interfaces.IView
import eu.kevin.core.extensions.applySystemInsetsMargin
import eu.kevin.core.extensions.applySystemInsetsPadding
import eu.kevin.core.extensions.getColorFromAttr
import eu.kevin.core.extensions.hideKeyboard
import eu.kevin.inapppayments.KevinPaymentsPlugin
import eu.kevin.inapppayments.R
import eu.kevin.inapppayments.databinding.FragmentPaymentConfirmationBinding

internal class PaymentConfirmationView(context: Context) : BaseView<FragmentPaymentConfirmationBinding>(context),
    IView<PaymentConfirmationState> {

    override val binding = FragmentPaymentConfirmationBinding.inflate(LayoutInflater.from(context), this)

    var delegate: PaymentConfirmationViewDelegate? = null

    init {
        binding.root.setBackgroundColor(context.getColorFromAttr(R.attr.kevinPrimaryBackgroundColor))
        with(binding.actionBar) {
            setNavigationOnClickListener {
                delegate?.onBackClicked()
            }
            applySystemInsetsPadding(top = true)
        }
        with(binding.confirmationWebView) {
            applySystemInsetsMargin(bottom = true)
            settings.javaScriptEnabled = true
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest
                ): Boolean {
                    if (request.url.toString().startsWith(KevinPaymentsPlugin.getCallbackUrl())) {
                        delegate?.onPaymentCompleted(request.url)
                    } else {
                        view.loadUrl(request.url.toString())
                    }
                    return true
                }
            }
        }
    }

    override fun onDetachedFromWindow() {
        binding.confirmationWebView.destroy()
        hideKeyboard()
        super.onDetachedFromWindow()
    }

    override fun render(state: PaymentConfirmationState) = with(binding) {
        if (state.url.isNotBlank()) {
            confirmationWebView.loadUrl(state.url)
        }
    }
}