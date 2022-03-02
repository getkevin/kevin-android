package eu.kevin.common.helpers

import android.content.Context
import eu.kevin.common.R
import eu.kevin.common.entities.KevinWebFrameColorsConfiguration
import eu.kevin.common.extensions.getColorFromAttr
import eu.kevin.common.extensions.isDarkMode
import eu.kevin.common.extensions.toHexColor
import eu.kevin.core.plugin.Kevin
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

object WebFrameHelper {

    fun getStyleAndLanguageQueryParameters(
        context: Context,
        deviceLocale: Locale
    ): String {
        return ""
            .appendQueryParameter("lang", getActiveLocaleCode(deviceLocale))
            .appendQueryParameter("cs", Json.encodeToString(getKevinWebFrameColorsConfigurationFromTheme(context)))
    }

    private fun getKevinWebFrameColorsConfigurationFromTheme(context: Context) : KevinWebFrameColorsConfiguration {
        with(context) {
            return KevinWebFrameColorsConfiguration(
                backgroundColor = getColorFromAttr(R.attr.kevinPrimaryBackgroundColor).toHexColor(),
                baseColor = getColorFromAttr(R.attr.kevinPrimaryBackgroundColor).toHexColor(),
                headingsColor = getColorFromAttr(R.attr.kevinPrimaryTextColor).toHexColor(),
                fontColor = getColorFromAttr(R.attr.kevinPrimaryTextColor).toHexColor(),
                bankIconColor = if (isDarkMode()) "white" else "default",
                defaultButtonColor = androidx.core.content.ContextCompat.getColor(this, R.color.kevin_blue).toHexColor()
            )
        }
    }

    private fun getActiveLocaleCode(defaultLocale: Locale): String {
        return Kevin.getLocale()?.language ?: defaultLocale.language
    }

    private fun String.appendQueryParameter(key: String, value: String): String {
        return if (this.isNotEmpty()) {
            "$this&$key=$value"
        } else {
            "$this$key=$value"
        }
    }
}