package eu.kevin.accounts.networking

import eu.kevin.core.networking.BaseApiFactory
import eu.kevin.core.networking.TokenDelegate
import okhttp3.logging.HttpLoggingInterceptor

/**
 * Factory class for creating [KevinAccountsClient]
 * @property baseUrl base url used for endpoints in [KevinAccountsClient]
 * @property userAgent userAgent of device
 * @property timeout time until network call will be timed out in milliseconds
 * @property httpLoggingInterceptorLevel level of interceptor to be used to log network calls
 */
class KevinAccountsClientFactory(
    baseUrl: String,
    userAgent: String,
    timeout: Long? = null,
    httpLoggingInterceptorLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.NONE,
) : BaseApiFactory<KevinAccountsClient>(
    baseUrl,
    userAgent,
    timeout,
    httpLoggingInterceptorLevel
) {

    /**
     * @return [KevinAccountsClient] with provided configuration
     */
    override fun createClient(tokenDelegate: TokenDelegate?): KevinAccountsClient {
        val service = createRetrofit(tokenDelegate).create(KevinAccountsService::class.java)
        return KevinAccountsApiClient(service)
    }
}