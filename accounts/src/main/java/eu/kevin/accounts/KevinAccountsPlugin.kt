package eu.kevin.accounts

import eu.kevin.core.plugin.KevinConfiguration
import eu.kevin.core.plugin.KevinPlugin

object KevinAccountsPlugin: KevinPlugin {

    private const val KEVIN_ACCOUNTS_PLUGIN_KEY = "kevinAccountsPluginKey"
    private lateinit var configuration: KevinConfiguration

    override fun configure(configuration: KevinConfiguration) {
        this.configuration = configuration
    }

    override fun isConfigured(): Boolean {
        return ::configuration.isInitialized
    }

    override fun getKey(): String {
        return KEVIN_ACCOUNTS_PLUGIN_KEY
    }

    override fun getTheme(): Int {
        return configuration.getTheme()
    }
}