package eu.kevin.accounts.networking

import eu.kevin.accounts.BuildConfig

internal object AccountsClientProvider {
    val kevinAccountsClient: KevinAccountsClient by lazy {
        KevinAccountsClientFactory(
            baseUrl = BuildConfig.KEVIN_ACCOUNTS_API_URL,
            userAgent = "",
            timeout = BuildConfig.HTTP_CLIENT_TIMEOUT,
            httpLoggingInterceptorLevel = BuildConfig.HTTP_LOGGING_INTERCEPTOR_LEVEL
        ).createClient(null)
    }
}