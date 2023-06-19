package eu.kevin.accounts.accountlinking.preferences

import android.content.Context

internal object AccountLinkingPreferencesProvider {

    private const val KEY = "KEVIN_SDK_ACCOUNT_PREFERENCES"

    fun providePreferences(context: Context): AccountLinkingPreferences {
        val sharedPreferences = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
        return AccountLinkingPreferences(sharedPreferences)
    }
}