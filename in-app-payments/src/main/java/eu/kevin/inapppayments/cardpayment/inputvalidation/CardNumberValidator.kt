package eu.kevin.inapppayments.cardpayment.inputvalidation

import eu.kevin.inapppayments.R

internal object CardNumberValidator {
    fun validate(cardNumber: String): ValidationResult {
        return when {
            cardNumber.isBlank() -> ValidationResult.Invalid(R.string.kevin_error_no_card_number)
            !isValidCardNumber(cardNumber) -> ValidationResult.Invalid(R.string.kevin_error_invalid_card_number)
            else -> ValidationResult.Valid
        }
    }

    private fun isValidCardNumber(cardNumber: String): Boolean {
        return cardNumber.length == 16 && isValidLuhn(cardNumber)
    }

    private fun isValidLuhn(cardNumber: String): Boolean {
        var isOdd = true
        var sum = 0

        for (index in cardNumber.length - 1 downTo 0) {
            val c = cardNumber[index]
            if (!c.isDigit()) {
                return false
            }

            var digitInteger = Character.getNumericValue(c)
            isOdd = !isOdd

            if (isOdd) {
                digitInteger *= 2
            }

            if (digitInteger > 9) {
                digitInteger -= 9
            }

            sum += digitInteger
        }

        return sum % 10 == 0
    }
}