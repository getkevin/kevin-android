package eu.kevin.inapppayments

import eu.kevin.core.plugin.KevinPlugin

object KevinPaymentsPlugin : KevinPlugin {

    private const val KEVIN_PAYMENTS_PLUGIN_KEY = "kevinPaymentsPluginKey"
    private lateinit var configuration: KevinPaymentsConfiguration

    fun configure(configuration: KevinPaymentsConfiguration) {
        this.configuration = configuration
    }

    override fun isConfigured(): Boolean {
        return ::configuration.isInitialized
    }

    override fun getKey(): String {
        return KEVIN_PAYMENTS_PLUGIN_KEY
    }

    fun getCallbackUrl(): String {
        return configuration.getCallbackUrl()
    }
}