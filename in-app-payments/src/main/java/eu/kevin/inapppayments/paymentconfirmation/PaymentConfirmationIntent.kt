package eu.kevin.inapppayments.paymentconfirmation

import android.net.Uri
import eu.kevin.common.architecture.interfaces.Intent

internal sealed class PaymentConfirmationIntent : Intent {
    data class Initialize(
        val configuration: PaymentConfirmationFragmentConfiguration,
        val webFrameQueryParameters: String
    ) : PaymentConfirmationIntent()
    object HandleBackClicked : PaymentConfirmationIntent()
    data class HandlePaymentCompleted(val uri: Uri) : PaymentConfirmationIntent()
}