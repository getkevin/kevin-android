package eu.kevin.inapppayments.paymentconfirmation

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.webkit.WebResourceRequest
import android.webkit.WebView
import androidx.core.view.updateLayoutParams
import androidx.webkit.WebViewClientCompat
import eu.kevin.common.architecture.BaseView
import eu.kevin.common.architecture.interfaces.IView
import eu.kevin.common.extensions.applySystemInsetsMargin
import eu.kevin.common.extensions.applySystemInsetsPadding
import eu.kevin.common.extensions.dp
import eu.kevin.common.extensions.getColorFromAttr
import eu.kevin.common.extensions.hideKeyboard
import eu.kevin.common.managers.KeyboardManager
import eu.kevin.inapppayments.KevinPaymentsPlugin
import eu.kevin.inapppayments.databinding.KevinFragmentPaymentConfirmationBinding
import eu.kevin.inapppayments.paymentconfirmation.PaymentConfirmationEvent.LoadWebPage

internal class PaymentConfirmationView(context: Context) :
    BaseView<KevinFragmentPaymentConfirmationBinding>(context),
    IView<PaymentConfirmationState, PaymentConfirmationEvent> {

    override val binding = KevinFragmentPaymentConfirmationBinding.inflate(LayoutInflater.from(context), this)

    var delegate: PaymentConfirmationViewDelegate? = null

    private var lastClickPosition: Int = 0

    init {
        setBackgroundColor(context.getColorFromAttr(android.R.attr.colorBackground))
        with(binding.actionBar) {
            setNavigationOnClickListener {
                delegate?.onBackClicked()
            }
            setNavigationContentDescription(eu.kevin.accounts.R.string.kevin_navigate_back_content_description)
            applySystemInsetsPadding(top = true)
        }

        binding.confirmationWebView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                lastClickPosition = v.height - event.y.toInt()
            }
            false
        }

        KeyboardManager(binding.root).apply {
            onKeyboardSizeChanged {
                binding.root.updateLayoutParams<MarginLayoutParams> {
                    bottomMargin = it
                }
            }
            onKeyboardVisibilityChanged {
                if (lastClickPosition < it) {
                    binding.confirmationWebView.scrollBy(0, (it - lastClickPosition) + dp(64))
                }
            }
        }

        with(binding.confirmationWebView) {
            setBackgroundColor(context.getColorFromAttr(android.R.attr.colorBackground))
            applySystemInsetsMargin(bottom = true)
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
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
        binding.confirmationWebView.destroy()
        hideKeyboard()
        super.onDetachedFromWindow()
    }

    override fun render(state: PaymentConfirmationState) = Unit

    override fun handleEvent(event: PaymentConfirmationEvent) {
        when (event) {
            is LoadWebPage -> {
                if (event.url.isNotBlank()) {
                    binding.confirmationWebView.loadUrl(event.url)
                }
            }
        }
    }

    fun handleWebViewBackPress(): Boolean {
        return if (binding.confirmationWebView.canGoBack()) {
            binding.confirmationWebView.goBack()
            true
        } else {
            false
        }
    }
}