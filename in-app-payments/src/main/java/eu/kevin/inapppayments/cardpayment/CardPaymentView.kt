package eu.kevin.inapppayments.cardpayment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import androidx.appcompat.widget.TooltipCompat
import androidx.core.view.updateLayoutParams
import androidx.core.widget.addTextChangedListener
import androidx.webkit.WebViewClientCompat
import eu.kevin.common.architecture.BaseView
import eu.kevin.common.architecture.interfaces.EventHandler
import eu.kevin.common.architecture.interfaces.IView
import eu.kevin.common.entities.LoadingState
import eu.kevin.common.extensions.applySystemInsetsMargin
import eu.kevin.common.extensions.applySystemInsetsPadding
import eu.kevin.common.extensions.fadeIn
import eu.kevin.common.extensions.fadeOut
import eu.kevin.common.extensions.getInputText
import eu.kevin.common.extensions.hideKeyboard
import eu.kevin.common.extensions.removeWhiteSpaces
import eu.kevin.common.extensions.setDebounceClickListener
import eu.kevin.common.extensions.setOnDoneActionListener
import eu.kevin.common.extensions.setOnNextActionListener
import eu.kevin.common.managers.KeyboardManager
import eu.kevin.inapppayments.KevinPaymentsPlugin
import eu.kevin.inapppayments.R
import eu.kevin.inapppayments.cardpayment.CardPaymentEvent.LoadWebPage
import eu.kevin.inapppayments.cardpayment.enums.CardPaymentMessage.CARD_PAYMENT_SUBMITTING
import eu.kevin.inapppayments.cardpayment.enums.CardPaymentMessage.HARD_REDIRECT_MODAL
import eu.kevin.inapppayments.cardpayment.enums.CardPaymentMessage.SOFT_REDIRECT_MODAL
import eu.kevin.inapppayments.cardpayment.events.CardPaymentWebEvent.HardRedirect
import eu.kevin.inapppayments.cardpayment.events.CardPaymentWebEvent.SoftRedirect
import eu.kevin.inapppayments.cardpayment.events.CardPaymentWebEvent.SubmittingCardData
import eu.kevin.inapppayments.cardpayment.inputformatters.CardNumberFormatter
import eu.kevin.inapppayments.cardpayment.inputformatters.DateFormatter
import eu.kevin.inapppayments.cardpayment.inputvalidation.ValidationResult
import eu.kevin.inapppayments.databinding.KevinFragmentCardPaymentBinding

internal class CardPaymentView(context: Context) :
    BaseView<KevinFragmentCardPaymentBinding>(context),
    IView<CardPaymentState>,
    EventHandler<CardPaymentEvent> {

    override var binding: KevinFragmentCardPaymentBinding? = KevinFragmentCardPaymentBinding.inflate(
        LayoutInflater.from(context),
        this
    )

    var delegate: CardPaymentViewDelegate? = null

    init {
        with(requireBinding()) {
            with(actionBar) {
                setNavigationOnClickListener {
                    delegate?.onBackClicked()
                }
                setNavigationContentDescription(eu.kevin.accounts.R.string.kevin_navigate_back_content_description)
                applySystemInsetsPadding(top = true)
            }

            with(cardholderNameInput) {
                editText?.addTextChangedListener {
                    error = null
                    isErrorEnabled = false
                }
                editText?.setOnNextActionListener {
                    expiryDateInput.requestFocus()
                }
            }

            with(cardNumberInput) {
                editText?.addTextChangedListener(CardNumberFormatter())
                editText?.addTextChangedListener {
                    error = null
                    isErrorEnabled = false
                    webView.evaluateJavascript(
                        "window.cardDetails.setCardNumber('${it?.toString() ?: ""}');"
                    ) {}
                }
                editText?.setOnNextActionListener {
                    cardholderNameInput.requestFocus()
                }
            }

            with(expiryDateInput) {
                editText?.addTextChangedListener(DateFormatter())
                editText?.addTextChangedListener {
                    error = null
                    isErrorEnabled = false
                }
                editText?.setOnNextActionListener {
                    cvvInput.requestFocus()
                }
            }

            with(cvvInput) {
                editText?.addTextChangedListener {
                    error = null
                    isErrorEnabled = false
                }
                editText?.setOnDoneActionListener {
                    handleContinueClick()
                }
            }

            continueButton.setDebounceClickListener {
                handleContinueClick()
            }
            scrollView.applySystemInsetsPadding(bottom = true)
        }

        KeyboardManager(requireBinding().root).apply {
            onKeyboardSizeChanged {
                requireBinding().root.updateLayoutParams<MarginLayoutParams> {
                    bottomMargin = it
                }
            }
        }

        with(requireBinding().cvvTooltipIcon) {
            TooltipCompat.setTooltipText(
                this,
                context.getString(R.string.kevin_window_card_payment_cvv_tooltip)
            )
            setDebounceClickListener {
                performLongClick()
            }
        }

        configureWebView()
    }

    @SuppressLint("JavascriptInterface")
    private fun configureWebView() {
        with(requireBinding().webView) {
            applySystemInsetsMargin(bottom = true)
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            addJavascriptInterface(
                object {
                    @JavascriptInterface
                    fun postMessage(message: String) {
                        when (message) {
                            SOFT_REDIRECT_MODAL.value -> {
                                delegate?.onWebEvent(
                                    SoftRedirect(
                                        requireBinding().cardNumberInput.getInputText().removeWhiteSpaces()
                                    )
                                )
                            }
                            HARD_REDIRECT_MODAL.value -> delegate?.onWebEvent(HardRedirect)
                            CARD_PAYMENT_SUBMITTING.value -> delegate?.onWebEvent(SubmittingCardData)
                        }
                    }
                },
                "AndroidHandler"
            )
            webViewClient = object : WebViewClientCompat() {
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
                    requireBinding().webView.evaluateJavascript("window.cardDetails.enableEventMessages();") {}
                    delegate?.onPageFinishedLoading()
                }
            }
        }
    }

    override fun render(state: CardPaymentState) {
        with(requireBinding()) {
            continueButton.isEnabled = state.isContinueEnabled
            amountView.text = state.amount?.getDisplayString(context)
            showCardDetails(state.showCardDetails)

            val loadingState = state.loadingState
            if (loadingState is LoadingState.Loading && loadingState.isLoading) {
                progressView.fadeIn()
            } else {
                progressView.fadeOut()
            }
        }
    }

    override fun handleEvent(event: CardPaymentEvent) {
        when (event) {
            is LoadWebPage -> {
                if (event.url.isNotBlank()) {
                    requireBinding().webView.loadUrl(event.url)
                }
            }
            is CardPaymentEvent.SubmitCardForm -> {
                submitCardForm(
                    event.cardholderName,
                    event.cardNumber,
                    event.expiryDate,
                    event.cvv
                )
            }
            is CardPaymentEvent.ShowFieldValidations -> {
                showInputFieldValidations(
                    event.cardholderNameValidation,
                    event.cardNumberValidation,
                    event.expiryDateValidation,
                    event.cvvValidation
                )
            }
            is CardPaymentEvent.SubmitUserRedirect -> {
                submitUserRedirect(event.shouldRedirect)
            }
        }
    }

    private fun submitCardForm(
        cardholderName: String,
        cardNumber: String,
        expiryDate: String,
        cvv: String
    ) {
        with(requireBinding().webView) {
            evaluateJavascript("window.cardDetails.setCardholderName('$cardholderName');") {}
            evaluateJavascript("window.cardDetails.setCardNumber('$cardNumber');") {}
            evaluateJavascript("window.cardDetails.setExpirationDate('$expiryDate');") {}
            evaluateJavascript("window.cardDetails.setCsc('$cvv');") {}
            evaluateJavascript("window.cardDetails.submitForm();") {}
        }
    }

    private fun showInputFieldValidations(
        cardholderNameValidation: ValidationResult,
        cardNumberValidation: ValidationResult,
        expiryDateValidation: ValidationResult,
        cvvValidation: ValidationResult
    ) {
        with(requireBinding()) {
            cardholderNameInput.error = cardholderNameValidation.getMessage(context)
            cardNumberInput.error = cardNumberValidation.getMessage(context)
            expiryDateInput.error = expiryDateValidation.getMessage(context)
            cvvInput.error = cvvValidation.getMessage(context)
        }
    }

    private fun submitUserRedirect(shouldRedirect: Boolean) {
        with(requireBinding()) {
            if (shouldRedirect) {
                webView.evaluateJavascript("window.cardDetails.confirmBank();") {}
            } else {
                webView.evaluateJavascript("window.cardDetails.cancelBank();") {}
            }
        }
    }

    private fun showCardDetails(show: Boolean) {
        with(requireBinding()) {
            if (show) {
                webView.fadeOut(250L) {
                    scrollView.fadeIn()
                }
            } else {
                scrollView.fadeOut(250L) {
                    webView.fadeIn()
                }
            }
        }
    }

    private fun handleContinueClick() {
        hideKeyboard()
        with(requireBinding()) {
            delegate?.onContinueClicked(
                cardholderNameInput.getInputText(),
                cardNumberInput.getInputText(),
                expiryDateInput.getInputText(),
                cvvInput.getInputText()
            )
        }
    }
}