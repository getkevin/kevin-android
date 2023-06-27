package eu.kevin.common.helpers

import android.content.Context
import androidx.core.content.ContextCompat
import eu.kevin.common.R
import eu.kevin.common.entities.KevinWebFrameConfiguration
import eu.kevin.common.extensions.getBooleanFromAttr
import eu.kevin.common.extensions.getColorFromAttr
import eu.kevin.common.extensions.getCurrentLocale
import eu.kevin.common.extensions.getStyleFromAttr
import eu.kevin.common.extensions.pxToDp
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
            val buttonConfiguration = getButtonConfiguration(context)
            return KevinWebFrameConfiguration(
                customLayout = listOf("hl"),
                backgroundColor = getColorFromAttr(android.R.attr.colorBackground).toHexColor(),
                baseColor = getColorFromAttr(android.R.attr.colorBackground).toHexColor(),
                headingsColor = getColorFromAttr(android.R.attr.textColorPrimary).toHexColor(),
                fontColor = getColorFromAttr(android.R.attr.textColorPrimary).toHexColor(),
                bankIconColor = if (useLightIcons) "white" else "default",
                buttonColor = buttonConfiguration.buttonColor.toHexColor(),
                buttonFontColor = buttonConfiguration.fontColor.toHexColor(),
                buttonRadius = "${pxToDp(buttonConfiguration.cornerRadius)}px",
            )
        }
    }

    private fun getActiveLocaleCode(context: Context): String {
        return Kevin.getLocale()?.language ?: context.getCurrentLocale().language
    }

    private fun getButtonConfiguration(context: Context): ButtonConfiguration {
        val attrs = intArrayOf(
            R.attr.backgroundTint,
            android.R.attr.textColor,
            R.attr.cornerRadius
        )
        val obtainedAttrs = context.obtainStyledAttributes(
            context.getStyleFromAttr(R.attr.kevinPrimaryButtonStyle),
            attrs
        )

        val buttonColor = obtainedAttrs.getColor(0, ContextCompat.getColor(context, R.color.kevin_blue))
        val fontColor = obtainedAttrs.getColor(1, ContextCompat.getColor(context, R.color.kevin_white))
        val cornerRadius = obtainedAttrs.getDimension(2, 8F).toInt()

        obtainedAttrs.recycle()

        return ButtonConfiguration(
            buttonColor = buttonColor,
            fontColor = fontColor,
            cornerRadius = cornerRadius
        )
    }

    private fun String.appendQueryParameter(key: String, value: String): String {
        return if (this.isNotEmpty()) {
            "$this&$key=$value"
        } else {
            "$key=$value"
        }
    }

    data class ButtonConfiguration(
        val buttonColor: Int,
        val fontColor: Int,
        val cornerRadius: Int
    )
}