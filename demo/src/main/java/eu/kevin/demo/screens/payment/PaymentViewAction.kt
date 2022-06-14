package eu.kevin.demo.screens.payment

import eu.kevin.demo.screens.payment.entities.ValidationResult
import eu.kevin.inapppayments.paymentsession.entities.PaymentSessionConfiguration

internal sealed class PaymentViewAction {
    data class OpenPaymentSession(
        val paymentSessionConfiguration: PaymentSessionConfiguration
    ) : PaymentViewAction()

    data class ShowFieldValidations(
        val emailValidationResult: ValidationResult,
        val amountValidationResult: ValidationResult,
        val termsAccepted: Boolean
    ) : PaymentViewAction()

    object ShowSuccessDialog : PaymentViewAction()

    object ResetFields : PaymentViewAction()
}