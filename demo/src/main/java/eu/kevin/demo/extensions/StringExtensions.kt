package eu.kevin.demo.extensions

import java.math.RoundingMode
import java.text.DecimalFormatSymbols
import java.util.*

fun String.toRepresentableBigDecimal(): String? {
    return this.toBigDecimalOrNull()?.setScale(2, RoundingMode.FLOOR)?.toString()
}

fun String.removeNumberSeparator(locale: Locale): String {
    val symbols = DecimalFormatSymbols(locale)
    val groupingSeparator = symbols.groupingSeparator.toString()
    return this.replace(groupingSeparator, "")
}