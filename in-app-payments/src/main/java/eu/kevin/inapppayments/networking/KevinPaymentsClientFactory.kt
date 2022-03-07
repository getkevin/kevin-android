package eu.kevin.inapppayments.networking

import eu.kevin.core.networking.BaseApiFactory
import io.ktor.client.features.logging.*

/**
 * Factory class for creating [KevinPaymentsClient]
 * @property baseUrl base url used for endpoints in [KevinPaymentsClient]
 * @property userAgent userAgent of device
 * @property timeout time until network call will be timed out in milliseconds
 * @property logLevel level of logs to be used to log network calls
 */
class KevinPaymentsClientFactory(
    baseUrl: String,
    userAgent: String,
    timeout: Int? = null,
    logLevel: LogLevel = LogLevel.NONE,
) : BaseApiFactory<KevinPaymentsClient>(
    baseUrl,
    userAgent,
    timeout,
    logLevel
) {

    /**
     * @return [KevinPaymentsClient] with provided configuration
     */
    override fun createClient(): KevinPaymentsClient {
        return KevinPaymentsApiClient(createKtorClient())
    }
}