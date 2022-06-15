package eu.kevin.demo.usecases

import eu.kevin.demo.auth.KevinApiClient
import eu.kevin.demo.auth.entities.RefreshAccessTokenRequest
import eu.kevin.demo.preferences.AccessToken
import eu.kevin.demo.preferences.AccountAccessTokenPreferences

internal class RefreshAccessTokenUseCase constructor(
    private val kevinAuthClient: KevinApiClient,
    private val accountAccessTokenPreferences: AccountAccessTokenPreferences
) {
    suspend fun refreshAccessToken(linkToken: String): AccessToken {
        val currentAccessToken = accountAccessTokenPreferences.getAccessToken(linkToken)
        val apiAccessToken = if (currentAccessToken != null) {
            kevinAuthClient.refreshAccessToken(
                request = RefreshAccessTokenRequest(
                    refreshToken = currentAccessToken.refreshToken
                )
            )
        } else {
            kevinAuthClient.getAccessToken(linkToken)
        }
        val accessToken = AccessToken(
            tokenType = apiAccessToken.tokenType,
            accessToken = apiAccessToken.accessToken,
            accessTokenExpiresAt = System.currentTimeMillis() + apiAccessToken.accessTokenExpiresIn * 1000,
            refreshToken = apiAccessToken.refreshToken,
            refreshTokenExpiresAt = System.currentTimeMillis() + apiAccessToken.refreshTokenExpiresIn * 1000
        )
        accountAccessTokenPreferences.putAccessToken(linkToken, accessToken)
        return accessToken
    }
}