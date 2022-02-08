package eu.kevin.inapppayments.cardpayment

import android.net.Uri
import eu.kevin.inapppayments.cardpayment.events.CardPaymentEvent

internal interface CardPaymentViewDelegate {
    fun onBackClicked()
    fun onContinueClicked(
        cardholderName: String,
        cardNumber: String,
        expiryDate: String,
        cvv: String
    )
    fun onPageStartLoading()
    fun onPageFinishedLoading()
    fun onPaymentResult(uri: Uri)
    fun onEvent(event: CardPaymentEvent)
}