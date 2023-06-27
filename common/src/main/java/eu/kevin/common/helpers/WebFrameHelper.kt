package eu.kevin.common.helpers

import android.content.Context
import androidx.core.content.ContextCompat
import eu.kevin.common.R
import eu.kevin.common.entities.KevinWebFrameConfiguration
import eu.kevin.common.extensions.getBooleanFromAttr
import eu.kevin.common.extensions.getColorFromAttr
import eu.kevin.common.extensions.getCurrentLocale
import eu.kevin.common.extensions.toHexColor
import eu.kevin.core.plugin.Kevin
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object WebFrameHelper {

    fun getStyleAndLanguageQueryParameters(context: Context): String {
        return ""
            .appendQueryParameter("lang", getActiveLocaleCode(context))
            .appendQueryParameter("cs", Json.encodeToString(getKevinWebFrameConfiguration(context)))
    }

    private fun getKevinWebFrameConfiguration(context: Context): KevinWebFrameConfiguration {
        with(context) {
            val useLightIcons = getBooleanFromAttr(R.attr.kevinUseLightBankIcons)
            return KevinWebFrameConfiguration(
                customLayout = listOf("hl"),
                backgroundColor = getColorFromAttr(android.R.attr.colorBackground).toHexColor(),
                baseColor = getColorFromAttr(android.R.attr.colorBackground).toHexColor(),
                headingsColor = getColorFromAttr(android.R.attr.textColorPrimary).toHexColor(),
                fontColor = getColorFromAttr(android.R.attr.textColorPrimary).toHexColor(),
                bankIconColor = if (useLightIcons) "white" else "default",
                defaultButtonColor = ContextCompat.getColor(this, R.color.kevin_blue).toHexColor()
            )
        }
    }

    private fun getActiveLocaleCode(context: Context): String {
        return Kevin.getLocale()?.language ?: context.getCurrentLocale().language
    }

    private fun String.appendQueryParameter(key: String, value: String): String {
        return if (this.isNotEmpty()) {
            "$this&$key=$value"
        } else {
            "$key=$value"
        }
    }
}