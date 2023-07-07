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
            val buttonConfiguration = getButtonConfiguration(context)
            val useLightIcons = getBooleanFromAttr(R.attr.kevinUseLightBankIcons)
            val backgroundColor = getColorFromAttr(android.R.attr.colorBackground).toHexColor()
            val textColor = getColorFromAttr(android.R.attr.textColorPrimary).toHexColor()

            return KevinWebFrameConfiguration(
                customLayout = listOf("hl", "cdd"),
                backgroundColor = backgroundColor,
                baseColor = backgroundColor,
                headingsColor = textColor,
                fontColor = textColor,
                bankIconColor = if (useLightIcons) "white" else "default",
                buttonColor = buttonConfiguration.buttonColor.toHexColor(),
                buttonFontColor = buttonConfiguration.fontColor.toHexColor(),
                buttonRadius = "${pxToDp(buttonConfiguration.cornerRadius)}px",
                inputBorderColor = textColor
            )
        }
    }

    private fun getActiveLocaleCode(context: Context): String {
        return Kevin.getLocale()?.language ?: context.getCurrentLocale().language
    }

    private fun getButtonConfiguration(context: Context): ButtonConfiguration {
        val attrs = intArrayOf(
            android.R.attr.textColor,
            R.attr.backgroundTint,
            R.attr.cornerRadius
        ).sortedArray()

        val obtainedAttrs = context.obtainStyledAttributes(
            context.getStyleFromAttr(R.attr.kevinPrimaryButtonStyle),
            attrs
        )

        val fontColor = obtainedAttrs.getColor(
            attrs.indexOf(android.R.attr.textColor),
            ContextCompat.getColor(context, R.color.kevin_white)
        )
        val buttonColor = obtainedAttrs.getColor(
            attrs.indexOf(R.attr.backgroundTint),
            ContextCompat.getColor(context, R.color.kevin_blue)
        )
        val cornerRadius = obtainedAttrs.getDimension(
            attrs.indexOf(R.attr.cornerRadius),
            8F
        )

        obtainedAttrs.recycle()

        return ButtonConfiguration(
            fontColor = fontColor,
            buttonColor = buttonColor,
            cornerRadius = cornerRadius.toInt()
        )
    }

    private fun String.appendQueryParameter(key: String, value: String): String {
        return if (this.isNotEmpty()) {
            "$this&$key=$value"
        } else {
            "$key=$value"
        }
    }

    private data class ButtonConfiguration(
        val buttonColor: Int,
        val fontColor: Int,
        val cornerRadius: Int
    )
}