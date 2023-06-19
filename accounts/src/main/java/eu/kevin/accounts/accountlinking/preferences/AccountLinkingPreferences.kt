package eu.kevin.accounts.accountlinking.preferences

import android.content.SharedPreferences

internal class AccountLinkingPreferences(
    private val sharedPreferences: SharedPreferences
) {

    private companion object Key {
        const val KEVIN_LAST_REDIRECT = "KEVIN_LAST_REDIRECT"
    }

    var lastRedirect: String?
        set(value) {
            sharedPreferences.edit().putString(KEVIN_LAST_REDIRECT, value).apply()
        }
        get() {
            return sharedPreferences.getString(KEVIN_LAST_REDIRECT, null)
        }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}