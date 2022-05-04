package eu.kevin.inapppayments.paymentconfirmation

import android.net.Uri

internal interface PaymentConfirmationViewDelegate {
    fun onBackClicked()
    fun onPaymentCompleted(uri: Uri)
    fun handleUri(uri: Uri)
    fun openAppIfAvailable(uri: Uri): Boolean
}