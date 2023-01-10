package eu.kevin.common.helpers

import eu.kevin.common.BuildConfig

object WebFrameHeadersHelper {

    private const val KEVIN_SDK_VERSION_KEY = "KevinAndroidSdk"

    fun appendTelemetryInfoToUserAgent(originalUserAgent: String): String {
        return originalUserAgent
            .addUserAgentDataField(key = KEVIN_SDK_VERSION_KEY, value = BuildConfig.KEVIN_SDK_VERSION)
    }

    private fun String.addUserAgentDataField(key: String, value: String): String {
        return this.plus(" $key/$value")
    }
}