package eu.kevin.accounts.accountlinking

import android.net.Uri
import eu.kevin.common.architecture.interfaces.IIntent
import eu.kevin.common.entities.KevinWebFrameColorsConfiguration
import java.util.*

internal sealed class AccountLinkingIntent : IIntent {
    data class Initialize(
        val configuration: AccountLinkingFragmentConfiguration,
        val kevinWebFrameColorsConfiguration: KevinWebFrameColorsConfiguration,
        val defaultLocale: Locale
    ) : AccountLinkingIntent()
    data class HandleAuthorization(val uri: Uri) : AccountLinkingIntent()
    object HandleBackClicked : AccountLinkingIntent()
}