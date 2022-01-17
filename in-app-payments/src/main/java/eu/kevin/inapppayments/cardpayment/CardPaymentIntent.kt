package eu.kevin.inapppayments.cardpayment

import eu.kevin.common.architecture.interfaces.IIntent

internal sealed class CardPaymentIntent : IIntent {
    data class Initialize(val configuration: CardPaymentFragmentConfiguration) : CardPaymentIntent()
}