package eu.kevin.inapppayments.paymentconfirmation

import android.net.Uri
import eu.kevin.common.architecture.interfaces.IIntent
import eu.kevin.inapppayments.paymentconfirmation.entities.PaymentConfirmationFrameColorsConfiguration
import java.util.*

internal sealed class PaymentConfirmationIntent : IIntent {
    data class Initialize(
        val configuration: PaymentConfirmationFragmentConfiguration,
        val kevinFrameColorsConfiguration: PaymentConfirmationFrameColorsConfiguration,
        val defaultLocale: Locale
    ) : PaymentConfirmationIntent()
    object HandleBackClicked : PaymentConfirmationIntent()
    data class HandlePaymentCompleted(val uri: Uri) : PaymentConfirmationIntent()
}