package eu.kevin.inapppayments.paymentconfirmation

import android.content.Context
import android.view.LayoutInflater
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import eu.kevin.core.architecture.BaseView
import eu.kevin.core.architecture.interfaces.IView
import eu.kevin.core.extensions.getColorFromAttr
import eu.kevin.inapppayments.R
import eu.kevin.inapppayments.databinding.FragmentPaymentConfirmationBinding

internal class PaymentConfirmationView(context: Context) : BaseView<FragmentPaymentConfirmationBinding>(context),
    IView<PaymentConfirmationState> {

    override val binding = FragmentPaymentConfirmationBinding.inflate(LayoutInflater.from(context), this)

    var delegate: PaymentConfirmationViewDelegate? = null

    init {
        binding.root.setBackgroundColor(context.getColorFromAttr(R.attr.kevinPrimaryBackgroundColor))
        binding.actionBar.setNavigationOnClickListener {
            delegate?.onBackClicked()
        }
        with(binding.confirmationWebView) {
            settings.javaScriptEnabled = true
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest
                ): Boolean {
                    if (request.url.toString().startsWith("https://redirect.getkevin.eu/payment.html")) {
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
        super.onDetachedFromWindow()
    }

    override fun render(state: PaymentConfirmationState) = with(binding) {
        if (state.url.isNotBlank()) {
            confirmationWebView.loadUrl(state.url)
        }
    }
}