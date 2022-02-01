package eu.kevin.inapppayments.cardpayment.events

internal sealed class CardPaymentEvent {
    data class SoftRedirect(val cardNumber: String) : CardPaymentEvent()
    object HardRedirect : CardPaymentEvent()
    object SubmittingCardData : CardPaymentEvent()
}