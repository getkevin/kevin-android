package eu.kevin.demo.screens.accountlinking

import android.content.Context
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import eu.kevin.accounts.KevinAccountsPlugin
import eu.kevin.accounts.accountsession.AccountSessionResult
import eu.kevin.common.architecture.BaseViewModel
import eu.kevin.core.entities.SessionResult
import eu.kevin.demo.auth.KevinApiClient
import eu.kevin.demo.auth.entities.InitiateAuthenticationRequest
import eu.kevin.demo.auth.enums.AuthenticationScope
import eu.kevin.demo.data.ClientProvider
import eu.kevin.demo.data.database.DatabaseProvider
import eu.kevin.demo.data.database.LinkedAccountsDao
import eu.kevin.demo.data.database.entities.LinkedAccount
import eu.kevin.demo.preferences.AccountAccessTokenPreferences
import eu.kevin.demo.routing.DemoRouter
import eu.kevin.demo.screens.accountactions.AccountActionsContract
import eu.kevin.demo.screens.accountactions.AccountActionsFragmentConfiguration
import eu.kevin.demo.screens.accountactions.entities.AccountAction
import eu.kevin.demo.screens.accountactions.enums.AccountActionType
import eu.kevin.demo.screens.accountlinking.AccountLinkingIntent.OnAccountActionSelected
import eu.kevin.demo.screens.accountlinking.AccountLinkingIntent.OnAccountLinkingResult
import eu.kevin.demo.screens.accountlinking.AccountLinkingIntent.OnStartAccountLinking
import eu.kevin.demo.screens.accountlinking.AccountLinkingIntent.OpenMenuForAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal class AccountLinkingViewModel(
    private val kevinApiClient: KevinApiClient,
    private val linkedAccountsDao: LinkedAccountsDao,
    private val accessTokenPreferences: AccountAccessTokenPreferences,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<AccountLinkingState, AccountLinkingIntent>(savedStateHandle) {

    private val _viewAction = Channel<AccountLinkingAction>(Channel.BUFFERED)
    val viewAction = _viewAction.receiveAsFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            linkedAccountsDao.getLinkedAccountsFlow().onEach { linkedAccounts ->
                updateState {
                    it.copy(
                        linkedAccounts = linkedAccounts
                    )
                }
            }.launchIn(this)
        }
    }

    override fun getInitialData() = AccountLinkingState()

    override suspend fun handleIntent(intent: AccountLinkingIntent) {
        when (intent) {
            is OnAccountActionSelected -> onAccountActionSelected(intent.action)
            is OnAccountLinkingResult -> onAccountLinkingResult(intent.accountSessionResult)
            is OnStartAccountLinking -> startAccountLinking()
            is OpenMenuForAccount -> openMenu(intent.id)
        }
    }

    private fun startAccountLinking() {
        viewModelScope.launch(Dispatchers.IO) {
            updateState {
                it.copy(isLoading = true)
            }
            try {
                val state = kevinApiClient.getAuthState(
                    InitiateAuthenticationRequest(
                        scopes = listOf(
                            AuthenticationScope.ACCOUNT_BASIC.value,
                            AuthenticationScope.PAYMENTS.value
                        ),
                        redirectUrl = KevinAccountsPlugin.getCallbackUrl()
                    )
                )
                _viewAction.send(AccountLinkingAction.OpenAccountLinkingSession(state))
            } catch (ignored: Exception) {
                updateState {
                    it.copy(isLoading = false)
                }
            }
        }
    }

    private suspend fun onAccountLinkingResult(accountSessionResult: SessionResult<AccountSessionResult>) {
        updateState {
            it.copy(isLoading = false)
        }
        when (accountSessionResult) {
            is SessionResult.Success -> {
                onAccountLinked(accountSessionResult.value)
            }
            is SessionResult.Canceled -> {}
            is SessionResult.Failure -> {}
        }
    }

    private fun onAccountActionSelected(action: AccountAction) {
        when (action.accountAction) {
            AccountActionType.REMOVE -> removeBank(action.id)
        }
    }

    private fun openMenu(id: Long) {
        DemoRouter.pushModalFragment(
            AccountActionsContract.getFragment(
                AccountActionsFragmentConfiguration(id)
            )
        )
    }

    private fun onAccountLinked(accountSessionResult: AccountSessionResult) {
        viewModelScope.launch(Dispatchers.IO) {
            linkedAccountsDao.delete(accountSessionResult.bank!!.id)
            linkedAccountsDao.insert(
                LinkedAccount(
                    bankName = accountSessionResult.bank!!.name,
                    logoUrl = accountSessionResult.bank!!.imageUri,
                    linkToken = accountSessionResult.authorizationCode,
                    bankId = accountSessionResult.bank!!.id
                )
            )
        }
    }

    private fun removeBank(bankId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            linkedAccountsDao.getById(bankId)?.let { linkedAccount ->
                linkedAccountsDao.getLinkedAccounts(linkedAccount.bankId).forEach {
                    accessTokenPreferences.removeAccessToken(it.linkToken)
                    linkedAccountsDao.delete(linkedAccount.bankId)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val context: Context,
        owner: SavedStateRegistryOwner
    ) :
        AbstractSavedStateViewModelFactory(owner, null) {
        override fun <T : ViewModel?> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return AccountLinkingViewModel(
                ClientProvider.kevinDemoApiClient,
                DatabaseProvider.getDatabase(context).linkedAccountsDao(),
                AccountAccessTokenPreferences(context),
                handle
            ) as T
        }
    }
}