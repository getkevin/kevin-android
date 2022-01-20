package eu.kevin.inapppayments.cardpayment.inputvalidation

import eu.kevin.inapppayments.R
import java.util.regex.Pattern

internal object CardExpiryDateValidator {
    private val expiryDatePattern = Pattern.compile("\\b(0[1-9]|1[0-2])/[0-9]{2}\\b")

    fun validate(expiryDate: String): ValidationResult {
        return when {
            expiryDate.isBlank() -> ValidationResult.Invalid(R.string.error_no_expiry_date)
            !expiryDatePattern.matcher(expiryDate).matches() -> ValidationResult.Invalid(R.string.error_invalid_expiry_date)
            else -> ValidationResult.Valid
        }
    }
}