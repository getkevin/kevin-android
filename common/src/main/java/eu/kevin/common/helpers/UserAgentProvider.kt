package eu.kevin.common.helpers

import eu.kevin.common.BuildConfig

object UserAgentProvider {
    fun getUserAgent(): String {
        return "Kevin Android SDK ${BuildConfig.KEVIN_SDK_VERSION} " +
            "${android.os.Build.MODEL} " +
            "Android ${android.os.Build.VERSION.RELEASE}"
    }
}