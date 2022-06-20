package eu.kevin.demo.screens.paymenttype

import eu.kevin.common.architecture.interfaces.IIntent
import eu.kevin.demo.screens.paymenttype.enums.DemoPaymentType

internal sealed class PaymentTypeIntent : IIntent {
    data class OnPaymentTypeChosen(val demoPaymentType: DemoPaymentType) : PaymentTypeIntent()
    data class Initialize(val paymentTypeFragmentConfiguration: PaymentTypeFragmentConfiguration) : PaymentTypeIntent()
}