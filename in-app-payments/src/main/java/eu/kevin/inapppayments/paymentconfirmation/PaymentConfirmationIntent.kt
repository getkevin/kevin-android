package eu.kevin.inapppayments.paymentconfirmation

import android.net.Uri
import eu.kevin.common.architecture.interfaces.IIntent
import eu.kevin.common.entities.KevinWebFrameColorsConfiguration
import java.util.*

internal sealed class PaymentConfirmationIntent : IIntent {
    data class Initialize(
        val configuration: PaymentConfirmationFragmentConfiguration,
        val kevinWebFrameColorsConfiguration: KevinWebFrameColorsConfiguration,
        val defaultLocale: Locale
    ) : PaymentConfirmationIntent()
    object HandleBackClicked : PaymentConfirmationIntent()
    data class HandlePaymentCompleted(val uri: Uri) : PaymentConfirmationIntent()
}