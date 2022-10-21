package eu.kevin.accounts.networking

import eu.kevin.accounts.BuildConfig
import eu.kevin.common.helpers.UserAgentProvider
import eu.kevin.core.plugin.Kevin

internal object AccountsClientProvider {
    val kevinAccountsClient: KevinAccountsClient by lazy {
        KevinAccountsClientFactory(
            baseUrl = if (Kevin.isSandbox()) {
                BuildConfig.KEVIN_SANDBOX_ACCOUNTS_API_URL
            } else {
                BuildConfig.KEVIN_ACCOUNTS_API_URL
            },
            userAgent = UserAgentProvider.getUserAgent(),
            timeout = BuildConfig.HTTP_CLIENT_TIMEOUT,
            logLevel = BuildConfig.HTTP_LOGGING_LEVEL
        ).createClient()
    }
}