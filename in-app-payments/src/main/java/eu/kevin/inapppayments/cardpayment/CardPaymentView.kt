package eu.kevin.inapppayments.cardpayment

import android.content.Context
import android.view.LayoutInflater
import android.webkit.WebViewClient
import eu.kevin.common.architecture.BaseView
import eu.kevin.common.architecture.interfaces.IView
import eu.kevin.common.extensions.applySystemInsetsMargin
import eu.kevin.common.extensions.applySystemInsetsPadding
import eu.kevin.common.extensions.getColorFromAttr
import eu.kevin.common.extensions.setDebounceClickListener
import eu.kevin.inapppayments.R
import eu.kevin.inapppayments.databinding.FragmentCardPaymentBinding

internal class CardPaymentView(context: Context) : BaseView<FragmentCardPaymentBinding>(context),
    IView<CardPaymentState> {

    override val binding = FragmentCardPaymentBinding.inflate(LayoutInflater.from(context), this)

    init {
        binding.root.setBackgroundColor(context.getColorFromAttr(R.attr.kevinPrimaryBackgroundColor))
        with(binding.actionBar) {
            setNavigationOnClickListener {
//                delegate?.onBackClicked()
            }
            setNavigationContentDescription(eu.kevin.accounts.R.string.navigate_back_content_description)
            applySystemInsetsPadding(top = true)
        }
        with(binding.continueButton) {
            setDebounceClickListener {
                binding.webView.evaluateJavascript("window.cardDetails.setCsc('123');") {
                    //  do something
                }
            }
            applySystemInsetsMargin(bottom = true)
        }

        with(binding.webView) {
            applySystemInsetsMargin(bottom = true)
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = object : WebViewClient() {

            }
        }
    }

    override fun render(state: CardPaymentState) {
        with(binding) {
            if (state.url.isNotBlank()) {
                webView.loadUrl(state.url)
            }
        }
    }
}