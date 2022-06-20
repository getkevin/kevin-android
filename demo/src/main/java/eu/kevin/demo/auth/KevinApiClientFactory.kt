package eu.kevin.demo.auth

import eu.kevin.core.networking.BaseApiFactory
import io.ktor.client.features.logging.LogLevel

internal class KevinApiClientFactory(
    baseUrl: String,
    userAgent: String,
    timeout: Int? = null,
    logLevel: LogLevel
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