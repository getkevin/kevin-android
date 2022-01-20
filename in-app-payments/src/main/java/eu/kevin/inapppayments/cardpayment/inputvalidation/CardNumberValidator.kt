package eu.kevin.inapppayments.cardpayment.inputvalidation

import eu.kevin.inapppayments.R

internal object CardNumberValidator {
    fun validate(cardNumber: String): ValidationResult {
        return when {
            cardNumber.isBlank() -> ValidationResult.Invalid(R.string.error_no_card_number)
            else -> ValidationResult.Valid
        }
    }
}