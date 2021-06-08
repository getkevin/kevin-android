package eu.kevin.core.plugin

interface KevinPlugin {
    fun getKey(): String
    fun isConfigured(): Boolean
    fun configure(configuration: KevinConfiguration)
    fun getTheme(): Int
}