package eu.kevin.demo.extensions

import java.math.RoundingMode

fun String.toRepresentableBigDecimal(): String? {
    return this.toBigDecimalOrNull()?.setScale(2, RoundingMode.FLOOR)?.toString()
}