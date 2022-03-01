package eu.kevin.accounts.accountlinking

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import eu.kevin.accounts.R
import eu.kevin.accounts.accountlinking.AccountLinkingIntent.*
import eu.kevin.common.architecture.BaseFragment
import eu.kevin.common.architecture.interfaces.IView
import eu.kevin.common.entities.KevinWebFrameColorsConfiguration
import eu.kevin.common.extensions.getColorFromAttr
import eu.kevin.common.extensions.getCurrentLocale
import eu.kevin.common.extensions.isDarkMode
import eu.kevin.common.extensions.toHexColor

internal class AccountLinkingFragment : BaseFragment<AccountLinkingState, AccountLinkingIntent, AccountLinkingViewModel>(),
    AccountLinkingViewDelegate {

    var configuration: AccountLinkingFragmentConfiguration? by savedState()

    private lateinit var view: AccountLinkingView

    override val viewModel: AccountLinkingViewModel by viewModels {
        AccountLinkingViewModel.Factory(this)
    }

    override fun onCreateView(context: Context): IView<AccountLinkingState> {
        return AccountLinkingView(context).also {
            it.delegate = this
            view = it
        }
    }

    override fun onAttached() {
        viewModel.intents.trySend(
            Initialize(
                configuration = configuration!!,
                defaultLocale = requireContext().getCurrentLocale(),
                kevinWebFrameColorsConfiguration = getKevinWebFrameColorsConfigurationFromTheme()
            )
        )
    }

    override fun onBackPressed(): Boolean {
        if (!view.handleWebViewBackPress()) {
            viewModel.intents.trySend(HandleBackClicked)
        }
        return true
    }

    private fun getKevinWebFrameColorsConfigurationFromTheme() =
        with(requireContext()) {
            KevinWebFrameColorsConfiguration(
                backgroundColor = getColorFromAttr(R.attr.kevinPrimaryBackgroundColor).toHexColor(),
                baseColor = getColorFromAttr(R.attr.kevinPrimaryBackgroundColor).toHexColor(),
                headingsColor = getColorFromAttr(R.attr.kevinPrimaryTextColor).toHexColor(),
                fontColor = getColorFromAttr(R.attr.kevinPrimaryTextColor).toHexColor(),
                bankIconColor = if (isDarkMode()) "white" else "default",
                defaultButtonColor = ContextCompat.getColor(this, R.color.kevin_blue).toHexColor()
            )
        }

    // AccountLinkingViewDelegate

    override fun onBackClicked() {
        if (!view.handleWebViewBackPress()) {
            viewModel.intents.trySend(HandleBackClicked)
        }
    }

    override fun onAuthorizationReceived(uri: Uri) {
        viewModel.intents.trySend(HandleAuthorization(uri))
    }

    override fun handleUri(uri: Uri) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        } catch (ignored: Exception) {}
    }
}