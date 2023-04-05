package com.example.account_linking

import android.app.Application
import eu.kevin.accounts.KevinAccountsConfiguration
import eu.kevin.accounts.KevinAccountsPlugin
import eu.kevin.core.plugin.Kevin

internal class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setupKevinSdk()
    }

    /**
     * Setup required account linking plugin in the Application class.
     * More info: https://developer.kevin.eu/home/mobile-sdk/android/installation
     */
    private fun setupKevinSdk() {
        /*
        When deep linking is enabled, native banks/services applications
        will be used during authorization process (if available).
        */
        Kevin.setDeepLinkingEnabled(true)

        // Initialize account linking plugin with callback url.
        val configuration = KevinAccountsConfiguration.builder()
            .setCallbackUrl("kevin://redirect.authorization")
            .build()

        KevinAccountsPlugin.configure(configuration)
    }
}