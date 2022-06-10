package eu.kevin.demo.payment

import eu.kevin.demo.auth.entities.ApiPayment
import eu.kevin.demo.payment.entities.ValidationResult
import eu.kevin.inapppayments.paymentsession.enums.PaymentType

internal sealed class PaymentViewAction {
    data class OpenPaymentSession(
        val payment: ApiPayment,
        val paymentType: PaymentType
    ) : PaymentViewAction()

    data class ShowFieldValidations(
        val emailValidationResult: ValidationResult,
        val amountValidationResult: ValidationResult,
        val termsAccepted: Boolean
    ) : PaymentViewAction()

    object ShowSuccessDialog : PaymentViewAction()

    object ResetFields : PaymentViewAction()
}