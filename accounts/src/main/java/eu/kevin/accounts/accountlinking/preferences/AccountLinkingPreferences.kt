package eu.kevin.accounts.accountlinking.preferences

import android.content.SharedPreferences

internal class AccountLinkingPreferences(
    private val sharedPreferences: SharedPreferences
) {

    private companion object Key {
        const val LAST_REDIRECT = "LAST_REDIRECT"
    }

    var lastRedirect: String?
        set(value) {
            sharedPreferences.edit().putString(LAST_REDIRECT, value).apply()
        }
        get() {
            return sharedPreferences.getString(LAST_REDIRECT, null)
        }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}