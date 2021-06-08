package eu.kevin.demo.auth

import eu.kevin.core.networking.BaseApiFactory
import eu.kevin.core.networking.TokenDelegate
import okhttp3.logging.HttpLoggingInterceptor

class KevinAuthClientFactory(
    baseUrl: String,
    userAgent: String,
    timeout: Long? = null,
    httpLoggingInterceptorLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BASIC,
) : BaseApiFactory<KevinAuthClient>(
    baseUrl,
    userAgent,
    timeout,
    httpLoggingInterceptorLevel
) {

    override fun createClient(tokenDelegate: TokenDelegate?): KevinAuthClient {
        val service = createRetrofit(tokenDelegate).create(KevinAuthService::class.java)
        return KevinAuthApiClient(service)
    }
}