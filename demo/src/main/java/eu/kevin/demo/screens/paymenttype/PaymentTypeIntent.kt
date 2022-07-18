package eu.kevin.demo.screens.paymenttype

import eu.kevin.common.architecture.interfaces.Intent
import eu.kevin.demo.screens.paymenttype.enums.DemoPaymentType

internal sealed class PaymentTypeIntent : Intent {
    data class OnPaymentTypeChosen(val demoPaymentType: DemoPaymentType) : PaymentTypeIntent()
    data class Initialize(val paymentTypeFragmentConfiguration: PaymentTypeFragmentConfiguration) : PaymentTypeIntent()
}