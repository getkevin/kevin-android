package eu.kevin.accounts

import eu.kevin.core.plugin.KevinPlugin

object KevinAccountsPlugin : KevinPlugin {

    private const val KEVIN_ACCOUNTS_PLUGIN_KEY = "kevinAccountsPluginKey"
    private lateinit var configuration: KevinAccountsConfiguration

    fun configure(configuration: KevinAccountsConfiguration) {
        this.configuration = configuration
    }

    override fun isConfigured(): Boolean {
        return ::configuration.isInitialized
    }

    override fun getKey(): String {
        return KEVIN_ACCOUNTS_PLUGIN_KEY
    }

    fun getCallbackUrl(): String {
        return configuration.getCallbackUrl()
    }

    fun isShowUnsupportedBanks(): Boolean {
        return configuration.isShowUnsupportedBanks()
    }
}