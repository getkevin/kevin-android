package com.example.ui_customization

import android.app.Application
import eu.kevin.accounts.KevinAccountsConfiguration
import eu.kevin.accounts.KevinAccountsPlugin
import eu.kevin.core.plugin.Kevin
import eu.kevin.samples.ui_customization.R

internal class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setupKevinSdk()
    }

    private fun setupKevinSdk() {

        // Set global kevin. theme to your custom one:
        Kevin.setTheme(R.style.CustomKevinTheme)

        // Configure account linking plugin with callback url.
        val configuration = KevinAccountsConfiguration.builder()
            .setCallbackUrl("kevin://redirect.authorization")
            .build()

        KevinAccountsPlugin.configure(configuration)
    }
}