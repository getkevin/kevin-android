package eu.kevin.demo.main

import eu.kevin.inapppayments.paymentsession.enums.PaymentType
import eu.kevin.demo.auth.entities.ApiPayment
import eu.kevin.demo.main.entities.ValidationResult

internal sealed class MainViewAction {
    data class OpenPaymentSession(
        val payment: ApiPayment,
        val paymentType: PaymentType
    ) : MainViewAction()

    data class ShowFieldValidations(
        val emailValidationResult: ValidationResult,
        val amountValidationResult: ValidationResult,
        val termsAccepted: Boolean
    ) : MainViewAction()
}