package eu.kevin.sample.samples.accountlinking

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.kevin.accounts.KevinAccountsPlugin
import eu.kevin.accounts.accountsession.AccountSessionResult
import eu.kevin.core.entities.SessionResult
import eu.kevin.sample.networking.KevinApiProvider
import eu.kevin.sample.networking.entities.authorization.AuthStateRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class AccountLinkingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AccountLinkingUiState())
    val uiState: StateFlow<AccountLinkingUiState> = _uiState.asStateFlow()

    private val kevinApi = KevinApiProvider.provideKevinApi()

    /**
     * For initialising account linking session, you'll need to use kevin. API to fetch authentication state.
     *
     * More info: https://developer.kevin.eu/home/mobile-sdk/backend/authentication
     */
    fun initiateAccountLinking() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val request = AuthStateRequest(
                    scopes = listOf("accounts_basic"),
                    redirectUrl = KevinAccountsPlugin.getCallbackUrl()
                )
                val state = kevinApi.fetchAuthState(request)

                _uiState.update {
                    it.copy(
                        accountLinkingState = state,
                        isLoading = false
                    )
                }
            } catch (error: Throwable) {
                _uiState.update {
                    it.copy(
                        userMessage = error.message ?: "Something went wrong",
                        isLoading = false
                    )
                }
            }
        }
    }

    /**
     * After the account is successfully linked, our SDK will return a result with authorizationCode
     * which can be used to fetch the tokens required for further AIS services like
     * fetching account's details, transactions or balances.
     *
     * More info: https://developer.kevin.eu/home/mobile-sdk/backend/authentication#getting-authentication-tokens
     */
    fun handleAccountLinkingResult(result: SessionResult<AccountSessionResult>) {
        when (result) {
            is SessionResult.Success -> _uiState.update {
                // Authorization code can be retrieved from callback's result.
                val authorizationCode = result.value.authorizationCode
                Log.d("MainViewModel", authorizationCode)

                val bankName = result.value.bank?.name
                it.copy(userMessage = "Success! $bankName has been linked.")
            }

            is SessionResult.Failure -> _uiState.update {
                // Account linking session has failed.
                // Handle failures in you application accordingly.
                it.copy(userMessage = "Account linking has failed!")
            }

            is SessionResult.Canceled -> _uiState.update {
                // Account linking session has been abandoned along the way.
                it.copy(userMessage = "Account linking has been cancelled.")
            }
        }
    }

    fun onAccountLinkingInitiated() {
        _uiState.update { it.copy(accountLinkingState = null) }
    }

    fun onUserMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }
}