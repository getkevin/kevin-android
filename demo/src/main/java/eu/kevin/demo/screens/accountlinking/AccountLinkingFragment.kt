package eu.kevin.demo.screens.accountlinking

import android.content.Context
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import eu.kevin.accounts.accountsession.AccountSessionContract
import eu.kevin.accounts.accountsession.entities.AccountSessionConfiguration
import eu.kevin.common.architecture.BaseFragment
import eu.kevin.common.architecture.interfaces.IView
import eu.kevin.common.extensions.setFragmentResultListener
import eu.kevin.core.enums.KevinCountry
import eu.kevin.demo.screens.accountactions.AccountActionsContract
import eu.kevin.demo.screens.accountlinking.AccountLinkingIntent.OnAccountActionSelected
import eu.kevin.demo.screens.accountlinking.AccountLinkingIntent.OnAccountLinkingResult
import eu.kevin.demo.screens.accountlinking.AccountLinkingIntent.OnStartAccountLinking
import eu.kevin.demo.screens.accountlinking.AccountLinkingIntent.OpenMenuForAccount
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal class AccountLinkingFragment :
    BaseFragment<AccountLinkingState, AccountLinkingIntent, AccountLinkingViewModel>(),
    AccountLinkingViewCallback {

    override val viewModel: AccountLinkingViewModel by activityViewModels {
        AccountLinkingViewModel.Factory(
            requireActivity().applicationContext,
            this
        )
    }

    private val linkAccount = registerForActivityResult(AccountSessionContract()) { result ->
        viewModel.intents.trySend(OnAccountLinkingResult(result))
    }

    override fun onCreateView(context: Context): IView<AccountLinkingState> {
        observeChanges()
        listenForAccountActionSelectedResult()
        return AccountLinkingView(context).also {
            it.callback = this
        }
    }

    private fun listenForAccountActionSelectedResult() {
        parentFragmentManager.setFragmentResultListener(AccountActionsContract, this) {
            viewModel.intents.trySend(OnAccountActionSelected(it))
        }
    }

    private fun observeChanges() {
        lifecycleScope.launchWhenStarted {
            viewModel.viewAction.onEach { action ->
                when (action) {
                    is AccountLinkingAction.OpenAccountLinkingSession -> {
                        openAccountLinkingSession(action.state)
                    }
                }
            }.launchIn(this)
        }
    }

    private fun openAccountLinkingSession(state: String) {
        val config = AccountSessionConfiguration.Builder(state)
            .setPreselectedCountry(KevinCountry.LITHUANIA)
            .setDisableCountrySelection(false)
            .build()
        linkAccount.launch(config)
    }

    override fun onLinkAccountClick() {
        viewModel.intents.trySend(OnStartAccountLinking)
    }

    override fun onOpenMenuClick(id: Long) {
        viewModel.intents.trySend(OpenMenuForAccount(id))
    }
}