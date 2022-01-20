package eu.kevin.inapppayments.cardpayment.events

internal sealed class CardPaymentEvent {
    object SoftRedirect : CardPaymentEvent()
    object HardRedirect : CardPaymentEvent()
    object SubmittingCardData : CardPaymentEvent()
}