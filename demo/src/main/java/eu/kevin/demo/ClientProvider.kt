package eu.kevin.demo

import eu.kevin.demo.BuildConfig.KEVIN_API_URL
import eu.kevin.demo.BuildConfig.KEVIN_MOBILE_DEMO_API
import eu.kevin.demo.auth.KevinApiClient
import eu.kevin.demo.auth.KevinApiClientFactory
import eu.kevin.demo.data.KevinDataClient
import eu.kevin.demo.data.KevinDataClientFactory
import io.ktor.client.features.logging.*

object ClientProvider {
    val kevinDataClient: KevinDataClient by lazy {
        KevinDataClientFactory(
            baseUrl = KEVIN_API_URL,
            userAgent = "",
            timeout = 120000,
            logLevel = LogLevel.INFO
        ).createClient(null)
    }

    val kevinAuthClient: KevinApiClient by lazy {
        KevinApiClientFactory(
            baseUrl = KEVIN_MOBILE_DEMO_API,
            userAgent = "",
            timeout = 120000,
            logLevel = LogLevel.INFO
        ).createClient(null)
    }
}