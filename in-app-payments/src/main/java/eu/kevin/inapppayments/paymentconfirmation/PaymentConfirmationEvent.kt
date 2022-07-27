package eu.kevin.inapppayments.paymentconfirmation

import eu.kevin.common.architecture.interfaces.IEvent

internal sealed class PaymentConfirmationEvent : IEvent {
    data class LoadWebPage(val url: String) : PaymentConfirmationEvent()
}