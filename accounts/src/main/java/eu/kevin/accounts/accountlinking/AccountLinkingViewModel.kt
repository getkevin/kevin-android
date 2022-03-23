package eu.kevin.accounts.accountlinking

import android.net.Uri
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import eu.kevin.accounts.BuildConfig
import eu.kevin.accounts.accountlinking.AccountLinkingIntent.*
import eu.kevin.accounts.accountsession.enums.AccountLinkingType
import eu.kevin.common.architecture.BaseViewModel
import eu.kevin.common.architecture.routing.GlobalRouter
import eu.kevin.common.extensions.appendQuery
import eu.kevin.common.fragment.FragmentResult
import eu.kevin.core.plugin.Kevin

internal class AccountLinkingViewModel(
    savedStateHandle: SavedStateHandle
) : BaseViewModel<AccountLinkingState, AccountLinkingIntent>(savedStateHandle) {

    override fun getInitialData() = AccountLinkingState()

    override suspend fun handleIntent(intent: AccountLinkingIntent) {
        when (intent) {
            is Initialize -> initialize(
                configuration = intent.configuration,
                webFrameQueryParameters = intent.webFrameQueryParameters
            )
            is HandleBackClicked -> GlobalRouter.popCurrentFragment()
            is HandleAuthorization -> handleAuthorizationReceived(intent.uri)
        }
    }

    private suspend fun initialize(
        configuration: AccountLinkingFragmentConfiguration,
        webFrameQueryParameters: String
    ) {
        val url = when (configuration.selectedAccountLinkingType) {
            AccountLinkingType.BANK -> {
                val baseLinkAccountUrl = if (Kevin.isSandbox()) {
                    BuildConfig.KEVIN_SANDBOX_LINK_ACCOUNT_URL
                } else {
                    BuildConfig.KEVIN_LINK_ACCOUNT_URL
                }
                baseLinkAccountUrl.format(
                    configuration.state,
                    configuration.selectedBankId
                ).appendQuery(webFrameQueryParameters)
            }
            else -> {
                val baseCardPaymentUrl = if (Kevin.isSandbox()) {
                    BuildConfig.KEVIN_SANDBOX_LINK_CARD_URL
                } else {
                    BuildConfig.KEVIN_LINK_CARD_URL
                }
                baseCardPaymentUrl.format(configuration.state)
                    .appendQuery(webFrameQueryParameters)
            }
        }

        updateState {
            it.copy(
                bankRedirectUrl = url,
                accountLinkingType = configuration.selectedAccountLinkingType
            )
        }
    }

    private fun handleAuthorizationReceived(uri: Uri) {
        val status = uri.getQueryParameter("status")
        if (status == "success") {
            val result = AccountLinkingFragmentResult(
                uri.getQueryParameter("requestId")!!,
                uri.getQueryParameter("code")!!
            )
            GlobalRouter.returnFragmentResult(
                AccountLinkingContract,
                FragmentResult.Success(result)
            )
        } else {
            GlobalRouter.returnFragmentResult(AccountLinkingContract, FragmentResult.Canceled)
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(owner: SavedStateRegistryOwner) :
        AbstractSavedStateViewModelFactory(owner, null) {
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