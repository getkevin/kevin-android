package eu.kevin.accounts.accountlinking

import android.net.Uri
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import eu.kevin.accounts.BuildConfig
import eu.kevin.accounts.accountlinking.AccountLinkingIntent.*
import eu.kevin.core.architecture.BaseViewModel
import eu.kevin.core.architecture.routing.GlobalRouter

internal class AccountLinkingViewModel(
    savedStateHandle: SavedStateHandle
) : BaseViewModel<AccountLinkingState, AccountLinkingIntent>(savedStateHandle) {

    override fun getInitialData() = AccountLinkingState()

    override suspend fun handleIntent(intent: AccountLinkingIntent) {
        when (intent) {
            is Initialize -> initialize(intent.configuration)
            is HandleBackClicked -> GlobalRouter.popCurrentFragment()
            is HandleAuthorization -> handleAuthorizationReceived(intent.uri)
        }
    }

    private suspend fun initialize(configuration: AccountLinkingFragmentConfiguration) {
        updateState {
            it.copy(bankRedirectUrl = BuildConfig.KEVIN_LINK_ACCOUNT_URL.format(
                configuration.state,
                configuration.selectedBankId
            ))
        }
    }

    private fun handleAuthorizationReceived(uri: Uri) {
        val result = AccountLinkingFragmentResult(
            uri.getQueryParameter("requestId")!!,
            uri.getQueryParameter("code")!!
        )
        GlobalRouter.returnFragmentResult(AccountLinkingFragment.Contract, result)
    }

    class Factory(owner: SavedStateRegistryOwner) : AbstractSavedStateViewModelFactory(owner, null) {
        override fun <T : ViewModel?> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return AccountLinkingViewModel(
                handle
            ) as T
        }
    }
}