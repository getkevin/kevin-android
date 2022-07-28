package eu.kevin.demo.auth

import eu.kevin.core.enums.KevinLogLevel
import eu.kevin.core.networking.BaseApiFactory

internal class KevinApiClientFactory(
    baseUrl: String,
    userAgent: String,
    timeout: Int? = null,
    logLevel: KevinLogLevel
) : BaseApiFactory<KevinApiClient>(
    baseUrl,
    userAgent,
    timeout,
    logLevel
) {

    override fun createClient(): KevinApiClient {
        return KevinClient(createKtorClient())
    }
}