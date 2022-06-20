package eu.kevin.demo.screens.payment.validation

import eu.kevin.demo.R
import eu.kevin.demo.screens.payment.entities.ValidationResult
import java.math.BigDecimal

internal object AmountValidator {

    fun validate(amount: String): ValidationResult {
        val bigDecimal = amount.toBigDecimalOrNull()
        return when {
            bigDecimal == null || bigDecimal <= BigDecimal.ZERO -> {
                ValidationResult.Invalid(R.string.kevin_window_main_amount_blank_error)
            }
            else -> ValidationResult.Valid
        }
    }
}