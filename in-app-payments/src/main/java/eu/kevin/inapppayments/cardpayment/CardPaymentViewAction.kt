package eu.kevin.inapppayments.cardpayment

import eu.kevin.common.architecture.interfaces.IIntent
import eu.kevin.inapppayments.cardpayment.inputvalidation.ValidationResult

internal sealed class CardPaymentViewAction : IIntent {
    data class ShowFieldValidations(
        val cardholderNameValidation: ValidationResult,
        val cardNumberValidation: ValidationResult,
        val expiryDateValidation: ValidationResult,
        val cvvValidation: ValidationResult
    ) : CardPaymentViewAction()

    data class SubmitCardForm(
        val cardholderName: String,
        val cardNumber: String,
        val expiryDate: String,
        val cvv: String
    ) : CardPaymentViewAction()
}