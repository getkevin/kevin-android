package eu.kevin.inapppayments.paymentconfirmation

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.view.updateLayoutParams
import eu.kevin.common.architecture.BaseView
import eu.kevin.common.architecture.interfaces.IView
import eu.kevin.common.extensions.*
import eu.kevin.common.managers.KeyboardManager
import eu.kevin.inapppayments.KevinPaymentsPlugin
import eu.kevin.inapppayments.R
import eu.kevin.inapppayments.databinding.FragmentPaymentConfirmationBinding

internal class PaymentConfirmationView(context: Context) : BaseView<FragmentPaymentConfirmationBinding>(context),
    IView<PaymentConfirmationState> {

    override val binding = FragmentPaymentConfirmationBinding.inflate(LayoutInflater.from(context), this)

    var delegate: PaymentConfirmationViewDelegate? = null

    private var lastClickPosition: Int = 0

    init {
        binding.root.setBackgroundColor(context.getColorFromAttr(R.attr.kevinPrimaryBackgroundColor))
        with(binding.actionBar) {
            setNavigationOnClickListener {
                delegate?.onBackClicked()
            }
            setNavigationContentDescription(eu.kevin.accounts.R.string.navigate_back_content_description)
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
            applySystemInsetsMargin(bottom = true)
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest
                ): Boolean {
                    val url = request.url.toString()
                    return if (url.startsWith("http://") || url.startsWith("https://")) {
                        if (url.startsWith(KevinPaymentsPlugin.getCallbackUrl())) {
                            delegate?.onPaymentCompleted(request.url)
                            true
                        } else {
                            false
                        }
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

    override fun render(state: PaymentConfirmationState) = with(binding) {
        if (state.url.isNotBlank()) {
            confirmationWebView.loadUrl(state.url)
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