package eu.kevin.demo.data

import eu.kevin.core.networking.BaseApiFactory
import eu.kevin.core.networking.TokenDelegate
import io.ktor.client.features.logging.*

class KevinDataClientFactory(
    baseUrl: String,
    userAgent: String,
    timeout: Int? = null,
    logLevel: LogLevel,
) : BaseApiFactory<KevinDataClient>(
    baseUrl,
    userAgent,
    timeout,
    logLevel
) {
    override fun createClient(tokenDelegate: TokenDelegate?): KevinDataClient {
        return KevinDataApiClient(createKtorClient())
    }
}