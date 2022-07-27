package eu.kevin.inapppayments.cardpayment

import eu.kevin.common.architecture.interfaces.IEvent
import eu.kevin.inapppayments.cardpayment.inputvalidation.ValidationResult

internal sealed class CardPaymentEvent : IEvent {

    data class LoadWebPage(val url: String) : CardPaymentEvent()

    data class ShowFieldValidations(
        val cardholderNameValidation: ValidationResult,
        val cardNumberValidation: ValidationResult,
        val expiryDateValidation: ValidationResult,
        val cvvValidation: ValidationResult
    ) : CardPaymentEvent()

    data class SubmitCardForm(
        val cardholderName: String,
        val cardNumber: String,
        val expiryDate: String,
        val cvv: String
    ) : CardPaymentEvent()

    data class SubmitUserRedirect(
        val shouldRedirect: Boolean
    ) : CardPaymentEvent()
}