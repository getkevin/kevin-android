package eu.kevin.demo.main

import eu.kevin.accounts.accountsession.enums.AccountLinkingType
import eu.kevin.demo.auth.entities.ApiPayment
import eu.kevin.demo.main.entities.ValidationResult
import eu.kevin.inapppayments.paymentsession.enums.PaymentType

internal sealed class MainViewAction {
    data class OpenPaymentSession(
        val payment: ApiPayment,
        val paymentType: PaymentType
    ) : MainViewAction()

    data class OpenAccountLinkingSession(
        val payment: ApiPayment,
        val accountLinkingType: AccountLinkingType
    ) : MainViewAction()

    data class ShowFieldValidations(
        val emailValidationResult: ValidationResult,
        val amountValidationResult: ValidationResult,
        val termsAccepted: Boolean
    ) : MainViewAction()

    object ShowSuccessDialog : MainViewAction()

    object ResetFields : MainViewAction()
}