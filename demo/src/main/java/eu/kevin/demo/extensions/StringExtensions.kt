package eu.kevin.demo.extensions

import java.math.RoundingMode
import java.text.DecimalFormatSymbols
import java.util.Locale

internal fun String.toRepresentableBigDecimal(): String? {
    return this.toBigDecimalOrNull()?.setScale(2, RoundingMode.FLOOR)?.toString()
}

internal fun String.removeNumberSeparator(locale: Locale): String {
    val symbols = DecimalFormatSymbols(locale)
    val groupingSeparator = symbols.groupingSeparator.toString()
    return this.replace(groupingSeparator, "")
}

internal fun String.replaceDecimalSeparatorWithDot(locale: Locale): String {
    val symbols = DecimalFormatSymbols(locale)
    val decimalSeparator = symbols.decimalSeparator.toString()
    return this.replace(decimalSeparator, ".")
}