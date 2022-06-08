package eu.kevin.inapppayments.cardpayment.inputvalidation

import eu.kevin.inapppayments.R

internal object CardholderNameValidator {
    fun validate(cardholderName: String): ValidationResult {
        return when {
            cardholderName.isBlank() -> ValidationResult.Invalid(R.string.kevin_error_no_cardholder_name)
            else -> ValidationResult.Valid
        }
    }
}