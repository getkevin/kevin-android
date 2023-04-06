package com.example.account_linking.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.account_linking.networking.KevinApiProvider
import com.example.account_linking.networking.entities.AuthStateRequest
import eu.kevin.accounts.KevinAccountsPlugin
import eu.kevin.accounts.accountsession.AccountSessionResult
import eu.kevin.core.entities.SessionResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val kevinApi = KevinApiProvider.provideKevinApi()

    /**
     * For the account linking, you will need to get a state using our API.
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

    fun handleAccountLinkingResult(result: SessionResult<AccountSessionResult>) {
        when (result) {
            is SessionResult.Success -> _uiState.update {
                val bankName = result.value.bank?.name
                it.copy(userMessage = "Success! $bankName account has been linked.")
            }
            is SessionResult.Failure -> _uiState.update {
                it.copy(userMessage = "Account linking has failed!")
            }
            is SessionResult.Canceled -> _uiState.update {
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