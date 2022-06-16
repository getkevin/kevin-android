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
import eu.kevin.accounts.accountsession.enums.AccountLinkingType
import eu.kevin.accounts.databinding.KevinFragmentAccountLinkingBinding
import eu.kevin.common.architecture.BaseView
import eu.kevin.common.architecture.interfaces.IView
import eu.kevin.common.extensions.applySystemInsetsMargin
import eu.kevin.common.extensions.applySystemInsetsPadding
import eu.kevin.common.extensions.dp
import eu.kevin.common.extensions.getColorFromAttr
import eu.kevin.common.extensions.hideKeyboard
import eu.kevin.common.managers.KeyboardManager

internal class AccountLinkingView(context: Context) :
    BaseView<KevinFragmentAccountLinkingBinding>(context),
    IView<AccountLinkingState> {

    override val binding = KevinFragmentAccountLinkingBinding.inflate(LayoutInflater.from(context), this)

    var delegate: AccountLinkingViewDelegate? = null

    private var lastClickPosition: Int = 0

    init {
        setBackgroundColor(context.getColorFromAttr(android.R.attr.colorBackground))
        with(binding.actionBar) {
            setNavigationOnClickListener {
                delegate?.onBackClicked()
            }
            setNavigationContentDescription(R.string.kevin_navigate_back_content_description)
            applySystemInsetsPadding(top = true)
        }

        binding.accountLinkWebView.setOnTouchListener { v, event ->
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
                    binding.accountLinkWebView.scrollBy(0, (it - lastClickPosition) + dp(64))
                }
            }
        }

        with(binding.accountLinkWebView) {
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
        binding.accountLinkWebView.destroy()
        hideKeyboard()
        super.onDetachedFromWindow()
    }

    override fun render(state: AccountLinkingState) = with(binding) {
        if (state.bankRedirectUrl.isNotBlank()) {
            accountLinkWebView.loadUrl(state.bankRedirectUrl)
        }
        if (state.accountLinkingType == AccountLinkingType.BANK) {
            binding.actionBar.title = context.getString(R.string.kevin_window_account_linking_title)
        } else {
            binding.actionBar.title = context.getString(R.string.kevin_window_account_linking_card_title)
        }
    }

    fun handleWebViewBackPress(): Boolean {
        return if (binding.accountLinkWebView.canGoBack()) {
            binding.accountLinkWebView.goBack()
            true
        } else {
            false
        }
    }
}