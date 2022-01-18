package eu.kevin.inapppayments.cardpayment

import eu.kevin.common.architecture.interfaces.IIntent

internal sealed class CardPaymentIntent : IIntent {
    object HandleBackClicked : CardPaymentIntent()
    data class Initialize(val configuration: CardPaymentFragmentConfiguration) : CardPaymentIntent()
}