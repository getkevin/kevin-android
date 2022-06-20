package eu.kevin.demo.screens.paymenttype.enums

import eu.kevin.inapppayments.paymentsession.enums.PaymentType

internal enum class DemoPaymentType {
    BANK, LINKED, CARD;

    fun toSdkPaymentType(): PaymentType {
        return when (this) {
            BANK, LINKED -> PaymentType.BANK
            CARD -> PaymentType.CARD
        }
    }
}