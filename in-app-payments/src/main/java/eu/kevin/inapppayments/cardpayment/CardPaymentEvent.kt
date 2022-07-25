package eu.kevin.inapppayments.cardpayment

import eu.kevin.common.architecture.interfaces.IEvent

internal sealed class CardPaymentEvent : IEvent {
    data class LoadWebPage(val url: String) : CardPaymentEvent()
}