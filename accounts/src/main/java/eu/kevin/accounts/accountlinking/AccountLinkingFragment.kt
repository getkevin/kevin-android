package eu.kevin.accounts.accountlinking

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import eu.kevin.accounts.accountlinking.AccountLinkingIntent.HandleAuthorization
import eu.kevin.accounts.accountlinking.AccountLinkingIntent.HandleBackClicked
import eu.kevin.accounts.accountlinking.AccountLinkingIntent.Initialize
import eu.kevin.common.architecture.BaseFragment
import eu.kevin.common.architecture.interfaces.DeepLinkHandler
import eu.kevin.common.architecture.interfaces.IView
import eu.kevin.common.extensions.launchOnRepeat
import eu.kevin.common.helpers.IntentHandlerHelper
import eu.kevin.common.helpers.WebFrameHelper
import eu.kevin.core.plugin.Kevin

internal class AccountLinkingFragment :
    BaseFragment<AccountLinkingState, AccountLinkingIntent, AccountLinkingViewModel>(),
    AccountLinkingViewDelegate,
    DeepLinkHandler {

    var configuration: AccountLinkingFragmentConfiguration? by savedState()

    private lateinit var view: AccountLinkingView

    override val viewModel: AccountLinkingViewModel by viewModels {
        AccountLinkingViewModel.Factory
    }

    override fun onCreateView(context: Context): IView<AccountLinkingState> {
        return AccountLinkingView(context).also {
            it.delegate = this
            view = it
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        launchOnRepeat {
            viewModel.events.collect { this@AccountLinkingFragment.view.handleEvent(it) }
        }
    }

    override fun onAttached() {
        viewModel.intents.trySend(
            Initialize(
                configuration = configuration!!,
                webFrameQueryParameters = WebFrameHelper.getStyleAndLanguageQueryParameters(
                    context = requireContext()
                )
            )
        )
    }

    override fun onBackPressed(): Boolean {
        if (!view.handleWebViewBackPress()) {
            viewModel.intents.trySend(HandleBackClicked)
        }
        return true
    }

    override fun handleDeepLink(uri: Uri) {
        viewModel.intents.trySend(HandleAuthorization(uri))
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
        } catch (ignored: Exception) {
        }
    }

    override fun openAppIfAvailable(uri: Uri): Boolean {
        if (!Kevin.isDeepLinkingEnabled()) return false
        try {
            val intent = IntentHandlerHelper.getIntentForUri(requireContext(), uri) ?: return false
            startActivity(intent)
            return true
        } catch (ignored: Exception) {
            return false
        }
    }
}