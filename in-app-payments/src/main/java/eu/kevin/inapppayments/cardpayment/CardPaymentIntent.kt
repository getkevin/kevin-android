package eu.kevin.inapppayments.cardpayment

import android.net.Uri
import eu.kevin.common.architecture.interfaces.IIntent
import eu.kevin.inapppayments.cardpayment.events.CardPaymentEvent

internal sealed class CardPaymentIntent : IIntent {
    object HandleBackClicked : CardPaymentIntent()
    object HandlePageStartLoading : CardPaymentIntent()
    object HandlePageFinishedLoading : CardPaymentIntent()
    object HandlePageLoadingError : CardPaymentIntent()
    data class Initialize(val configuration: CardPaymentFragmentConfiguration) : CardPaymentIntent()
    data class HandleOnContinueClicked(
        val cardholderName: String,
        val cardNumber: String,
        val expiryDate: String,
        val cvv: String
    ) : CardPaymentIntent()
    data class HandlePaymentResult(
        val uri: Uri
    ) : CardPaymentIntent()
    data class HandleCardPaymentEvent(val event: CardPaymentEvent) : CardPaymentIntent()
    data class HandleUserSoftRedirect(val shouldRedirect: Boolean) : CardPaymentIntent()
}