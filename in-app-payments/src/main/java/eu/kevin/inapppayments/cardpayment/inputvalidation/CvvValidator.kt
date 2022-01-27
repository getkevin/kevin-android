package eu.kevin.inapppayments.cardpayment.inputvalidation

import eu.kevin.inapppayments.R
import java.util.regex.Pattern

internal object CvvValidator {
    private val cvvPattern = Pattern.compile("^[0-9]{3,4}\$")

    fun validate(cvv: String): ValidationResult {
        return when {
            cvv.isBlank() -> ValidationResult.Invalid(R.string.error_no_cvv)
            !cvvPattern.matcher(cvv).matches() -> ValidationResult.Invalid(R.string.error_invalid_cvv)
            else -> ValidationResult.Valid
        }
    }
}