package eu.kevin.inapppayments.cardpayment.inputvalidation

import eu.kevin.inapppayments.R

internal object CvvValidator {
    fun validate(cvv: String): ValidationResult {
        return when {
            cvv.isBlank() -> ValidationResult.Invalid(R.string.error_no_cvv)
            cvv.length < 3 -> ValidationResult.Invalid(R.string.error_short_cvv)
            else -> ValidationResult.Valid
        }
    }
}