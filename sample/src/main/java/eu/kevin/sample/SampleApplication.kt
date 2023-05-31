package eu.kevin.sample

import android.app.Application
import eu.kevin.accounts.KevinAccountsConfiguration
import eu.kevin.accounts.KevinAccountsPlugin
import eu.kevin.core.plugin.Kevin
import eu.kevin.inapppayments.KevinPaymentsConfiguration
import eu.kevin.inapppayments.KevinPaymentsPlugin

internal class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setupKevinSdk()
        setupKevinSdkTheme()
    }

    /**
     * Setup required account linking / payments plugin(s) in your Application class.
     *
     * More info: https://developer.kevin.eu/home/mobile-sdk/android/installation
     */
    private fun setupKevinSdk() {
        /*
        When deep linking is enabled, native banks/services applications
        will be used during authorization process (if available).
        */
        Kevin.setDeepLinkingEnabled(true)

        // Configure account linking plugin with callback url.
        val configurationAccounts = KevinAccountsConfiguration.builder()
            .setCallbackUrl("kevin://redirect.authorization")
            .build()

        KevinAccountsPlugin.configure(configurationAccounts)

        // Configure payments with callback url (if you are using SDK to initiate payments).
        val configurationPayments = KevinPaymentsConfiguration.builder()
            .setCallbackUrl("kevin://redirect.payments")
            .build()

        KevinPaymentsPlugin.configure(configurationPayments)
    }

    /**
     * Only relevant for UI customization sample.
     *
     * Visit themes.xml resource file to see how you can override styling parameters and change the looks of
     * SDK components to match your needs.
     *
     * Uncomment line below to set custom theme for all other samples.
     * [Kevin.setTheme] applies your custom styling to SDK components.
     */
    private fun setupKevinSdkTheme() {
//        Kevin.setTheme(R.style.CustomKevinTheme)
    }
}