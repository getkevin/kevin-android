package eu.kevin.accounts.accountlinking

import android.net.Uri

internal interface AccountLinkingViewDelegate {
    fun onBackClicked()
    fun onAuthorizationReceived(uri: Uri)
}