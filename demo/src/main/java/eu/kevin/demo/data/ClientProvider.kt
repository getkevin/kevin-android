package eu.kevin.demo.data

import eu.kevin.demo.BuildConfig
import eu.kevin.demo.BuildConfig.KEVIN_API_URL
import eu.kevin.demo.BuildConfig.KEVIN_MOBILE_DEMO_API
import eu.kevin.demo.auth.KevinApiClient
import eu.kevin.demo.auth.KevinApiClientFactory

internal object ClientProvider {
    val kevinApiClient: KevinDataClient by lazy {
        KevinDataClientFactory(
            baseUrl = KEVIN_API_URL,
            userAgent = "",
            timeout = 120000,
            logLevel = BuildConfig.HTTP_LOGGING_LEVEL
        ).createClient()
    }

    val kevinDemoApiClient: KevinApiClient by lazy {
        KevinApiClientFactory(
            baseUrl = KEVIN_MOBILE_DEMO_API,
            userAgent = "",
            timeout = 120000,
            logLevel = BuildConfig.HTTP_LOGGING_LEVEL
        ).createClient()
    }
}