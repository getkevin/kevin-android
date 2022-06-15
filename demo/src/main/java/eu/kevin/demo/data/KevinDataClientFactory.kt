package eu.kevin.demo.data

import eu.kevin.core.networking.BaseApiFactory
import io.ktor.client.features.logging.LogLevel

internal class KevinDataClientFactory(
    baseUrl: String,
    userAgent: String,
    timeout: Int? = null,
    logLevel: LogLevel
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