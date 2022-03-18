package eu.kevin.inapppayments.networking

import eu.kevin.accounts.BuildConfig
import eu.kevin.core.plugin.Kevin

internal object KevinPaymentsClientProvider {
    val kevinPaymentsClient: KevinPaymentsClient by lazy {
        KevinPaymentsClientFactory(
            baseUrl = if (Kevin.isSandbox()) BuildConfig.KEVIN_SANDBOX_ACCOUNTS_API_URL else BuildConfig.KEVIN_ACCOUNTS_API_URL,
            userAgent = "",
            timeout = BuildConfig.HTTP_CLIENT_TIMEOUT,
            logLevel = BuildConfig.HTTP_LOGGING_LEVEL
        ).createClient()
    }
}