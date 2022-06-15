package eu.kevin.demo.usecases

import eu.kevin.demo.preferences.AccessToken
import eu.kevin.demo.preferences.AccountAccessTokenPreferences

internal class GetAccessTokenForAccountUseCase(
    private val refreshAccessTokenUseCase: RefreshAccessTokenUseCase,
    private val accountAccessTokenPreferences: AccountAccessTokenPreferences
) {
    suspend fun getAccessTokenForAccount(linkToken: String): AccessToken {
        return accountAccessTokenPreferences.getAccessToken(linkToken)
            ?: refreshAccessTokenUseCase.refreshAccessToken(linkToken)
    }
}