package eu.kevin.inapppayments.paymentconfirmation

import android.net.Uri
import eu.kevin.core.architecture.interfaces.IIntent

internal sealed class PaymentConfirmationIntent : IIntent {
    data class Initialize(val configuration: PaymentConfirmationFragmentConfiguration) : PaymentConfirmationIntent()
    object HandleBackClicked : PaymentConfirmationIntent()
    data class HandlePaymentCompleted(val uri: Uri) : PaymentConfirmationIntent()
}