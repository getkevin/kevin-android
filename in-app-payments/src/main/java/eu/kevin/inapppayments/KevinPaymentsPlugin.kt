package eu.kevin.inapppayments

import eu.kevin.core.plugin.KevinConfiguration
import eu.kevin.core.plugin.KevinPlugin

object KevinPaymentsPlugin: KevinPlugin {

    private const val KEVIN_PAYMENTS_PLUGIN_KEY = "kevinPaymentsPluginKey"
    private lateinit var configuration: KevinConfiguration

    override fun configure(configuration: KevinConfiguration) {
        this.configuration = configuration
    }

    override fun isConfigured(): Boolean {
        return ::configuration.isInitialized
    }

    override fun getKey(): String {
        return KEVIN_PAYMENTS_PLUGIN_KEY
    }

    override fun getTheme(): Int {
        return configuration.getTheme()
    }
}