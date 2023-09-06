package eu.kevin.accounts.accountlinking

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import eu.kevin.accounts.BuildConfig
import eu.kevin.accounts.KevinAccountsPlugin
import eu.kevin.accounts.accountlinking.AccountLinkingEvent.LoadWebPage
import eu.kevin.accounts.accountlinking.AccountLinkingIntent.HandleAuthorization
import eu.kevin.accounts.accountlinking.AccountLinkingIntent.HandleBackClicked
import eu.kevin.accounts.accountlinking.AccountLinkingIntent.Initialize
import eu.kevin.accounts.accountsession.enums.AccountLinkingType
import eu.kevin.common.architecture.BaseViewModel
import eu.kevin.common.architecture.routing.GlobalRouter
import eu.kevin.common.extensions.appendQuery
import eu.kevin.common.fragment.FragmentResult
import eu.kevin.core.plugin.Kevin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

internal class AccountLinkingViewModel(
    savedStateHandle: SavedStateHandle
) : BaseViewModel<AccountLinkingState, AccountLinkingIntent>(savedStateHandle) {

    private val _events = Channel<AccountLinkingEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

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
        val url = when (configuration.linkingType) {
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
        }

        updateState {
            it.copy(
                accountLinkingType = configuration.linkingType,
                isProcessing = false
            )
        }

        initializeWebUrl(url)
    }

    private suspend fun initializeWebUrl(url: String) {
        val isDeepLinkingEnabled = Kevin.isDeepLinkingEnabled()

        /*
        We are checking for an existing redirect to avoid some
        possible extensive redirects after process death restoration.
         */
        if (isDeepLinkingEnabled && savedStateHandle.get<String>("redirect_url") == url) {
            updateState {
                it.copy(isProcessing = true)
            }
            return
        }

        if (isDeepLinkingEnabled) {
            savedStateHandle["redirect_url"] = url
        }

        _events.send(LoadWebPage(url))
    }

    private fun handleAuthorizationReceived(uri: Uri) {
        if (!uri.toString().startsWith(KevinAccountsPlugin.getCallbackUrl())) return

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
            GlobalRouter.returnFragmentResult(
                AccountLinkingContract,
                FragmentResult.Failure(
                    error = Exception("Account linking was canceled!")
                )
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AccountLinkingViewModel(
                    savedStateHandle = createSavedStateHandle()
                )
            }
        }
    }
}