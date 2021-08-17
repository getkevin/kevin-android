package eu.kevin.accounts.accountlinking

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.view.updateLayoutParams
import eu.kevin.accounts.KevinAccountsPlugin
import eu.kevin.accounts.R
import eu.kevin.accounts.databinding.FragmentAccountLinkingBinding
import eu.kevin.common.architecture.BaseView
import eu.kevin.common.architecture.interfaces.IView
import eu.kevin.common.extensions.*
import eu.kevin.common.managers.KeyboardManager

internal class AccountLinkingView(context: Context) : BaseView<FragmentAccountLinkingBinding>(context),
    IView<AccountLinkingState> {

    override val binding = FragmentAccountLinkingBinding.inflate(LayoutInflater.from(context), this)

    var delegate: AccountLinkingViewDelegate? = null

    private var lastClickPosition: Int = 0

    init {
        binding.root.setBackgroundColor(context.getColorFromAttr(R.attr.kevinPrimaryBackgroundColor))

        with(binding.actionBar) {
            setNavigationOnClickListener {
                delegate?.onBackClicked()
            }
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
            applySystemInsetsMargin(bottom = true)
            settings.javaScriptEnabled = true
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest
                ): Boolean {
                    if (request.url.toString().startsWith(KevinAccountsPlugin.getCallbackUrl())) {
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
        hideKeyboard()
        super.onDetachedFromWindow()
    }

    override fun render(state: AccountLinkingState) = with(binding) {
        if (state.bankRedirectUrl.isNotBlank()) {
            accountLinkWebView.loadUrl(state.bankRedirectUrl)
        }
    }
}