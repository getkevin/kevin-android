package eu.kevin.demo.main

import eu.kevin.inapppayments.paymentsession.enums.PaymentType
import eu.kevin.demo.auth.entities.ApiPayment

sealed class MainViewAction {
    data class OpenAccountLinkingSession(val state: String) : MainViewAction()
    data class OpenPaymentSession(val payment: ApiPayment, val paymentType: PaymentType) : MainViewAction()
}