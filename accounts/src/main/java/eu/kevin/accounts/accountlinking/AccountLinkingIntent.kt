package eu.kevin.accounts.accountlinking

import android.net.Uri
import eu.kevin.common.architecture.interfaces.Intent

internal sealed class AccountLinkingIntent : Intent {
    data class Initialize(
        val configuration: AccountLinkingFragmentConfiguration,
        val webFrameQueryParameters: String
    ) : AccountLinkingIntent()
    data class HandleAuthorization(val uri: Uri) : AccountLinkingIntent()
    object HandleBackClicked : AccountLinkingIntent()
}