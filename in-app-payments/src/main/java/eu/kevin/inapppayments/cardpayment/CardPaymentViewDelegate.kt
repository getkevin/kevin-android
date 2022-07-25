package eu.kevin.inapppayments.cardpayment

import android.net.Uri
import eu.kevin.inapppayments.cardpayment.events.CardPaymentWebEvent

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
    fun onWebEvent(event: CardPaymentWebEvent)
}