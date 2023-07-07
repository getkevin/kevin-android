package eu.kevin.accounts.accountlinking

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.webkit.WebResourceRequest
import android.webkit.WebView
import androidx.core.view.updateLayoutParams
import androidx.webkit.WebViewClientCompat
import eu.kevin.accounts.KevinAccountsPlugin
import eu.kevin.accounts.R
import eu.kevin.accounts.accountlinking.AccountLinkingEvent.LoadWebPage
import eu.kevin.accounts.accountsession.enums.AccountLinkingType
import eu.kevin.accounts.databinding.KevinFragmentAccountLinkingBinding
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

internal class AccountLinkingView(context: Context) :
    BaseView<KevinFragmentAccountLinkingBinding>(context),
    IView<AccountLinkingState>,
    EventHandler<AccountLinkingEvent> {

    override var binding: KevinFragmentAccountLinkingBinding? = KevinFragmentAccountLinkingBinding.inflate(
        LayoutInflater.from(context),
        this
    )

    var delegate: AccountLinkingViewDelegate? = null

    private var lastClickPosition: Int = 0

    init {
        setBackgroundColor(context.getColorFromAttr(android.R.attr.colorBackground))
        with(requireBinding().actionBar) {
            setNavigationOnClickListener {
                delegate?.onBackClicked()
            }
            setNavigationContentDescription(R.string.kevin_navigate_back_content_description)
            applySystemInsetsPadding(top = true)
        }

        requireBinding().accountLinkWebView.setOnTouchListener { v, event ->
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
                    requireBinding().accountLinkWebView.scrollBy(0, (it - lastClickPosition) + dp(64))
                }
            }
        }

        with(requireBinding().accountLinkWebView) {
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
                    return if (url.startsWith(KevinAccountsPlugin.getCallbackUrl())) {
                        delegate?.onAuthorizationReceived(request.url)
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
        requireBinding().accountLinkWebView.destroy()
        hideKeyboard()
        super.onDetachedFromWindow()
    }

    override fun render(state: AccountLinkingState) = with(requireBinding()) {
        if (state.accountLinkingType == AccountLinkingType.BANK) {
            actionBar.title = context.getString(R.string.kevin_window_account_linking_title)
        }
        with(state.isProcessing) {
            accountLinkProgressView.visibility = if (this) VISIBLE else GONE
        }
    }

    override fun handleEvent(event: AccountLinkingEvent) {
        when (event) {
            is LoadWebPage -> {
                if (event.url.isNotBlank()) {
                    requireBinding().accountLinkWebView.loadUrl(event.url)
                }
            }
        }
    }

    fun handleWebViewBackPress(): Boolean {
        return if (requireBinding().accountLinkWebView.canGoBack()) {
            requireBinding().accountLinkWebView.goBack()
            true
        } else {
            false
        }
    }
}