package eu.kevin.demo.auth

import eu.kevin.core.networking.BaseApiFactory
import eu.kevin.core.networking.TokenDelegate
import io.ktor.client.features.logging.*

class KevinAuthClientFactory(
    baseUrl: String,
    userAgent: String,
    timeout: Int? = null,
    logLevel: LogLevel,
) : BaseApiFactory<KevinAuthClient>(
    baseUrl,
    userAgent,
    timeout,
    logLevel
) {

    override fun createClient(tokenDelegate: TokenDelegate?): KevinAuthClient {
        return KevinAuthApiClient(createKtorClient())
    }
}