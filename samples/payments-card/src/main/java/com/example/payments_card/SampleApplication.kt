package com.example.payments_card

import android.app.Application
import eu.kevin.inapppayments.KevinPaymentsConfiguration
import eu.kevin.inapppayments.KevinPaymentsPlugin

internal class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setupKevinSdk()
    }

    /**
     * Setup required payments plugin in your Application class.
     *
     * More info: https://developer.kevin.eu/home/mobile-sdk/android/installation
     */
    private fun setupKevinSdk() {
        // Configure payments plugin with callback url.
        val configuration = KevinPaymentsConfiguration.builder()
            .setCallbackUrl("kevin://redirect.authorization")
            .build()

        KevinPaymentsPlugin.configure(configuration)
    }
}