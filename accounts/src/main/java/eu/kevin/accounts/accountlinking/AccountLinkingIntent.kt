package eu.kevin.accounts.accountlinking

import android.net.Uri
import eu.kevin.common.architecture.interfaces.IIntent

internal sealed class AccountLinkingIntent : IIntent {
    data class Initialize(
        val configuration: AccountLinkingFragmentConfiguration,
        val webFrameQueryParameters: String
    ) : AccountLinkingIntent()

    data class HandleAuthorization(val uri: Uri) : AccountLinkingIntent()
    data object HandleBackClicked : AccountLinkingIntent()
}