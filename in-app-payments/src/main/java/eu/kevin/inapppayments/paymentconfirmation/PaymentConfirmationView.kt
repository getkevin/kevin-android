package eu.kevin.inapppayments.paymentconfirmation

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.webkit.*
import androidx.core.view.updateLayoutParams
import eu.kevin.core.architecture.BaseView
import eu.kevin.core.architecture.interfaces.IView
import eu.kevin.core.extensions.*
import eu.kevin.core.helpers.KeyboardManager
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
                    binding.confirmationWebView.scrollBy(0, (it - lastClickPosition) + 150)
                }
            }
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