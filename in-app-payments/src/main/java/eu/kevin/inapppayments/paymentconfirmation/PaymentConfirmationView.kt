package eu.kevin.inapppayments.paymentconfirmation

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.webkit.WebResourceRequest
import android.webkit.WebView
import androidx.core.view.updateLayoutParams
import androidx.webkit.WebViewClientCompat
import eu.kevin.common.architecture.BaseView
import eu.kevin.common.architecture.interfaces.EventHandler
import eu.kevin.common.architecture.interfaces.IView
import eu.kevin.common.extensions.applySystemInsetsMargin
import eu.kevin.common.extensions.applySystemInsetsPadding
import eu.kevin.common.extensions.dp
import eu.kevin.common.extensions.getColorFromAttr
import eu.kevin.common.extensions.hideKeyboard
import eu.kevin.common.helpers.WebFrameHeadersHelper
import eu.kevin.common.managers.KeyboardManager
import eu.kevin.inapppayments.KevinPaymentsPlugin
import eu.kevin.inapppayments.databinding.KevinFragmentPaymentConfirmationBinding
import eu.kevin.inapppayments.paymentconfirmation.PaymentConfirmationEvent.LoadWebPage

internal class PaymentConfirmationView(context: Context) :
    BaseView<KevinFragmentPaymentConfirmationBinding>(context),
    IView<PaymentConfirmationState>,
    EventHandler<PaymentConfirmationEvent> {

    override var binding: KevinFragmentPaymentConfirmationBinding? = KevinFragmentPaymentConfirmationBinding.inflate(
        LayoutInflater.from(context),
        this
    )

    var delegate: PaymentConfirmationViewDelegate? = null

    private var lastClickPosition: Int = 0

    init {
        setBackgroundColor(context.getColorFromAttr(android.R.attr.colorBackground))
        with(requireBinding().actionBar) {
            setNavigationOnClickListener {
                delegate?.onBackClicked()
            }
            setNavigationContentDescription(eu.kevin.accounts.R.string.kevin_navigate_back_content_description)
            applySystemInsetsPadding(top = true)
        }

        requireBinding().confirmationWebView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                lastClickPosition = v.height - event.y.toInt()
            }
            false
        }

        KeyboardManager(requireBinding().root).apply {
            onKeyboardSizeChanged {
                requireBinding().root.updateLayoutParams<MarginLayoutParams> {
                    bottomMargin = it
                }
            }
            onKeyboardVisibilityChanged {
                if (lastClickPosition < it) {
                    requireBinding().confirmationWebView.scrollBy(0, (it - lastClickPosition) + dp(64))
                }
            }
        }

        with(requireBinding().confirmationWebView) {
            setBackgroundColor(context.getColorFromAttr(android.R.attr.colorBackground))
            applySystemInsetsMargin(bottom = true)
            with(settings) {
                javaScriptEnabled = true
                domStorageEnabled = true
                userAgentString = WebFrameHeadersHelper.appendTelemetryInfoToUserAgent(
                    originalUserAgent = userAgentString
                )
            }
            webViewClient = object : WebViewClientCompat() {
                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest
                ): Boolean {
                    val url = request.url.toString()
                    return if (url.startsWith(KevinPaymentsPlugin.getCallbackUrl())) {
                        delegate?.onPaymentCompleted(request.url)
                        true
                    } else if (url.startsWith("http://") || url.startsWith("https://")) {
                        delegate?.openAppIfAvailable(request.url) ?: false
                    } else {
                        delegate?.handleUri(request.url)
                        true
                    }
                }
            }
        }
    }

    override fun onDetachedFromWindow() {
        requireBinding().confirmationWebView.destroy()
        hideKeyboard()
        super.onDetachedFromWindow()
    }

    override fun render(state: PaymentConfirmationState) {
        with(requireBinding()) {
            progressView.visibility = if (state.isProcessing) VISIBLE else GONE
        }
    }

    override fun handleEvent(event: PaymentConfirmationEvent) {
        when (event) {
            is LoadWebPage -> {
                if (event.url.isNotBlank()) {
                    requireBinding().confirmationWebView.loadUrl(event.url)
                }
            }
        }
    }

    fun handleWebViewBackPress(): Boolean {
        return if (requireBinding().confirmationWebView.canGoBack()) {
            requireBinding().confirmationWebView.goBack()
            true
        } else {
            false
        }
    }
}