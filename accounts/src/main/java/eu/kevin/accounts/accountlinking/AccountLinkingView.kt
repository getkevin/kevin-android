package eu.kevin.accounts.accountlinking

import android.content.Context
import android.view.LayoutInflater
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import eu.kevin.accounts.R
import eu.kevin.accounts.databinding.FragmentAccountLinkingBinding
import eu.kevin.core.architecture.BaseView
import eu.kevin.core.architecture.interfaces.IView
import eu.kevin.core.extensions.getColorFromAttr

internal class AccountLinkingView(context: Context) : BaseView<FragmentAccountLinkingBinding>(context),
    IView<AccountLinkingState> {

    override val binding = FragmentAccountLinkingBinding.inflate(LayoutInflater.from(context), this)

    var delegate: AccountLinkingViewDelegate? = null

    init {
        binding.root.setBackgroundColor(context.getColorFromAttr(R.attr.kevinPrimaryBackgroundColor))
        binding.actionBar.setNavigationOnClickListener {
            delegate?.onBackClicked()
        }
        with(binding.accountLinkWebView) {
            settings.javaScriptEnabled = true
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest
                ): Boolean {
                    if (request.url.toString().startsWith("https://redirect.getkevin.eu/authorization.html")) {
                        delegate?.onAuthorizationReceived(request.url)
                    } else {
                        view.loadUrl(request.url.toString())
                    }
                    return true
                }
            }
        }
    }

    override fun onDetachedFromWindow() {
        binding.accountLinkWebView.destroy()
        super.onDetachedFromWindow()
    }

    override fun render(state: AccountLinkingState) = with(binding) {
        if (state.bankRedirectUrl.isNotBlank()) {
            accountLinkWebView.loadUrl(state.bankRedirectUrl)
        }
    }
}