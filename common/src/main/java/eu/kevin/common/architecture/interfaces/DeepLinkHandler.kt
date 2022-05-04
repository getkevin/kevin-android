package eu.kevin.common.architecture.interfaces

import android.net.Uri

interface DeepLinkHandler {
    fun handleDeepLink(uri: Uri)
}