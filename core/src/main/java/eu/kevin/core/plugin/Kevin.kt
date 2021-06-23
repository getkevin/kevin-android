package eu.kevin.core.plugin

import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

object Kevin {

    private val configurationLock = AtomicBoolean(false)
    private val plugins = mutableSetOf<KevinPlugin>()
    private var locale: Locale? = null

    fun addPlugin(plugin: KevinPlugin) {
        synchronized(this) {
            plugins.add(plugin)
        }
    }

    fun setLocale(locale: Locale?) {
        synchronized(this) {
            this.locale = locale
        }
    }

    fun getLocale(): Locale? {
        return locale
    }

    fun configure(configuration: KevinConfiguration) {
        synchronized(configurationLock) {
            if (configurationLock.get()) {
                throw KevinException("Kevin configuration can be called only once!")
            }
            plugins.forEach {
                it.configure(configuration)
            }
            configurationLock.set(true)
        }
    }
}