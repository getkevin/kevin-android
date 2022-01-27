package eu.kevin.inapppayments.cardpayment

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.webkit.*
import androidx.appcompat.widget.TooltipCompat
import androidx.core.view.updateLayoutParams
import androidx.core.widget.addTextChangedListener
import androidx.transition.TransitionManager
import eu.kevin.common.architecture.BaseView
import eu.kevin.common.architecture.interfaces.IView
import eu.kevin.common.entities.LoadingState
import eu.kevin.common.extensions.*
import eu.kevin.common.managers.KeyboardManager
import eu.kevin.inapppayments.KevinPaymentsPlugin
import eu.kevin.inapppayments.R
import eu.kevin.inapppayments.cardpayment.events.CardPaymentEvent.*
import eu.kevin.inapppayments.cardpayment.inputformatters.CardNumberFormatter
import eu.kevin.inapppayments.cardpayment.inputformatters.DateFormatter
import eu.kevin.inapppayments.cardpayment.inputvalidation.ValidationResult
import eu.kevin.inapppayments.databinding.FragmentCardPaymentBinding

internal class CardPaymentView(context: Context) : BaseView<FragmentCardPaymentBinding>(context),
    IView<CardPaymentState> {

    override val binding = FragmentCardPaymentBinding.inflate(LayoutInflater.from(context), this)

    var delegate: CardPaymentViewDelegate? = null
    private var previousStateUrl: String? = null

    init {
        binding.root.setBackgroundColor(context.getColorFromAttr(R.attr.kevinPrimaryBackgroundColor))
        with(binding.actionBar) {
            setNavigationOnClickListener {
                delegate?.onBackClicked()
            }
            setNavigationContentDescription(eu.kevin.accounts.R.string.navigate_back_content_description)
            applySystemInsetsPadding(top = true)
        }

        with(binding) {
            cardholderNameInput.editText?.addTextChangedListener {
                cardholderNameInput.error = null
                cardholderNameInput.isErrorEnabled = false
            }
            cardNumberInput.editText?.addTextChangedListener(CardNumberFormatter())
            cardNumberInput.editText?.addTextChangedListener {
                cardNumberInput.error = null
                cardNumberInput.isErrorEnabled = false
                binding.webView.evaluateJavascript(
                    "window.cardDetails.setCardNumber('${it?.toString() ?: ""}');"
                ) {}
            }
            expiryDateInput.editText?.addTextChangedListener(DateFormatter())
            expiryDateInput.editText?.addTextChangedListener {
                expiryDateInput.error = null
                expiryDateInput.isErrorEnabled = false
            }
            cvvInput.editText?.addTextChangedListener {
                cvvInput.error = null
                cvvInput.isErrorEnabled = false
            }
        }

        binding.continueButton.setDebounceClickListener {
            hideKeyboard()
            delegate?.onContinueClicked(
                binding.cardholderNameInput.getInputText(),
                binding.cardNumberInput.getInputText(),
                binding.expiryDateInput.getInputText(),
                binding.cvvInput.getInputText()
            )
        }

        binding.scrollView.applySystemInsetsPadding(bottom = true)
        KeyboardManager(binding.root).apply {
            onKeyboardSizeChanged {
                binding.root.updateLayoutParams<MarginLayoutParams> {
                    bottomMargin = it
                }
            }
        }

        with(binding.webView) {
            applySystemInsetsMargin(bottom = true)
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            addJavascriptInterface(object {
                @JavascriptInterface
                fun postMessage(message: String) {
                    when (message) {
                        "SOFT_REDIRECT_MODAL" -> delegate?.onEvent(SoftRedirect)
                        "HARD_REDIRECT_MODAL" -> delegate?.onEvent(HardRedirect)
                        "CARD_PAYMENT_SUBMITTING" -> delegate?.onEvent(SubmittingCardData)
                    }
                }
            }, "AndroidHandler")
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest
                ): Boolean {
                    val url = request.url.toString()
                    return if (url.startsWith(KevinPaymentsPlugin.getCallbackUrl())) {
                        delegate?.onPaymentResult(request.url)
                        true
                    } else {
                        false
                    }
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    delegate?.onPageStartLoading()
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    binding.webView.evaluateJavascript("window.cardDetails.enableEventMessages();") {}
                    delegate?.onPageFinishedLoading()
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    delegate?.onPageLoadingError()
                }
            }
        }

        with(binding.cvvTooltipIcon) {
            TooltipCompat.setTooltipText(
                this,
                context.getString(R.string.window_card_payment_cvv_tooltip)
            )
            setDebounceClickListener {
                performLongClick()
            }
        }
    }

    override fun render(state: CardPaymentState) {
        with(binding) {
            if (state.url.isNotBlank() && previousStateUrl != state.url) {
                previousStateUrl = state.url
                webView.loadUrl(state.url)
            }
            binding.continueButton.isEnabled = state.isContinueEnabled
            showCardDetails(state.showCardDetails)

            val loadingState = state.loadingState
            if (loadingState is LoadingState.Loading && loadingState.isLoading) {
                binding.progressView.fadeIn()
            } else {
                binding.progressView.fadeOut()
            }
        }
    }

    fun submitCardForm(
        cardholderName: String,
        cardNumber: String,
        expiryDate: String,
        cvv: String
    ) {
        with(binding.webView) {
            evaluateJavascript("window.cardDetails.setCardholderName('$cardholderName');") {}
            evaluateJavascript("window.cardDetails.setCardNumber('$cardNumber');") {}
            evaluateJavascript("window.cardDetails.setExpirationDate('$expiryDate');") {}
            evaluateJavascript("window.cardDetails.setCsc('$cvv');") {}
            evaluateJavascript("window.cardDetails.submitForm();") {}
        }
    }

    fun showInputFieldValidations(
        cardholderNameValidation: ValidationResult,
        cardNumberValidation: ValidationResult,
        expiryDateValidation: ValidationResult,
        cvvValidation: ValidationResult
    ) {
        with(binding) {
            cardholderNameInput.error = cardholderNameValidation.getMessage(context)
            cardNumberInput.error = cardNumberValidation.getMessage(context)
            expiryDateInput.error = expiryDateValidation.getMessage(context)
            cvvInput.error = cvvValidation.getMessage(context)
        }
    }

    fun submitUserRedirect(shouldRedirect: Boolean) {
        if (shouldRedirect) {
            binding.webView.evaluateJavascript("window.cardDetails.confirmBank();") {}
        } else {
            binding.webView.evaluateJavascript("window.cardDetails.cancelBank();") {}
        }
    }

    private fun showCardDetails(show: Boolean) {
        TransitionManager.beginDelayedTransition(this)
        if (show) {
            binding.webView.visibility = GONE
            binding.scrollView.visibility = VISIBLE
        } else {
            binding.webView.visibility = VISIBLE
            binding.scrollView.visibility = GONE
        }
    }
}