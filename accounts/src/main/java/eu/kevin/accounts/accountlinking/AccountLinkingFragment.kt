package eu.kevin.accounts.accountlinking

import android.content.Context
import android.net.Uri
import androidx.fragment.app.viewModels
import eu.kevin.accounts.accountlinking.AccountLinkingIntent.*
import eu.kevin.common.architecture.BaseFragment
import eu.kevin.common.architecture.interfaces.IView

internal class AccountLinkingFragment : BaseFragment<AccountLinkingState, AccountLinkingIntent, AccountLinkingViewModel>(),
    AccountLinkingViewDelegate {

    var configuration: AccountLinkingFragmentConfiguration? by savedState()

    override val viewModel: AccountLinkingViewModel by viewModels {
        AccountLinkingViewModel.Factory(this)
    }

    override fun onCreateView(context: Context): IView<AccountLinkingState> {
        return AccountLinkingView(context).also {
            it.delegate = this
        }
    }

    override fun onAttached() {
        viewModel.intents.trySend(Initialize(configuration!!))
    }

    override fun onBackPressed(): Boolean {
        viewModel.intents.trySend(HandleBackClicked)
        return true
    }

    // AccountLinkingViewDelegate

    override fun onBackClicked() {
        viewModel.intents.trySend(HandleBackClicked)
    }

    override fun onAuthorizationReceived(uri: Uri) {
        viewModel.intents.trySend(HandleAuthorization(uri))
    }
}