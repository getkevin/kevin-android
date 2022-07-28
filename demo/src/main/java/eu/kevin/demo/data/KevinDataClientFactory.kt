package eu.kevin.demo.data

import eu.kevin.core.enums.KevinLogLevel
import eu.kevin.core.networking.BaseApiFactory

internal class KevinDataClientFactory(
    baseUrl: String,
    userAgent: String,
    timeout: Int? = null,
    logLevel: KevinLogLevel
) : BaseApiFactory<KevinDataClient>(
    baseUrl,
    userAgent,
    timeout,
    logLevel
) {
    override fun createClient(): KevinDataClient {
        return KevinDataApiClient(createKtorClient())
    }
}