package eu.kevin.accounts.accountlinking

import android.net.Uri
import eu.kevin.core.architecture.interfaces.IIntent

internal sealed class AccountLinkingIntent : IIntent {
    data class Initialize(val configuration: AccountLinkingFragmentConfiguration) : AccountLinkingIntent()
    data class HandleAuthorization(val uri: Uri) : AccountLinkingIntent()
    object HandleBackClicked : AccountLinkingIntent()
}