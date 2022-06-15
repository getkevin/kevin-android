package eu.kevin.demo.preferences

import android.content.Context
import eu.kevin.demo.base.SecurePreferences

internal class AccountAccessTokenPreferences(context: Context) : SecurePreferences(context, "tokens") {

    fun putAccessToken(linkToken: String, accessToken: AccessToken) {
        put(linkToken, accessToken)
    }

    fun getAccessToken(linkToken: String): AccessToken? {
        return get(linkToken)
    }

    fun removeAccessToken(linkToken: String) {
        remove(linkToken)
    }
}