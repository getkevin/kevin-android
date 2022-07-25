package eu.kevin.inapppayments.cardpayment.events

internal sealed class CardPaymentWebEvent {
    data class SoftRedirect(val cardNumber: String) : CardPaymentWebEvent()
    object HardRedirect : CardPaymentWebEvent()
    object SubmittingCardData : CardPaymentWebEvent()
}