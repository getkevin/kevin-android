package eu.kevin.accounts.networking

import eu.kevin.core.enums.KevinLogLevel
import eu.kevin.core.networking.BaseApiFactory

/**
 * Factory class for creating [KevinAccountsClient]
 * @property baseUrl base url used for endpoints in [KevinAccountsClient]
 * @property userAgent userAgent of device
 * @property timeout time until network call will be timed out in milliseconds
 * @property logLevel level of logs to be used to log network calls
 */
class KevinAccountsClientFactory(
    baseUrl: String,
    userAgent: String,
    timeout: Int? = null,
    logLevel: KevinLogLevel = KevinLogLevel.NONE
) : BaseApiFactory<KevinAccountsClient>(
    baseUrl,
    userAgent,
    timeout,
    logLevel
) {

    /**
     * @return [KevinAccountsClient] with provided configuration
     */
    override fun createClient(): KevinAccountsClient {
        return KevinAccountsApiClient(createKtorClient())
    }
}