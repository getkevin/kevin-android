package eu.kevin.core.plugin

import androidx.annotation.StyleRes
import eu.kevin.core.R
import java.util.*

object Kevin {

    private var locale: Locale? = null
    private var theme: Int = R.style.Theme_Kevin_Base
    private var isSandbox: Boolean = false
    private var isDeepLinkingEnabled: Boolean = true

    fun setLocale(locale: Locale?) {
        synchronized(this) {
            this.locale = locale
        }
    }

    fun setTheme(@StyleRes theme: Int) {
        synchronized(this) {
            this.theme = theme
        }
    }

    fun setSandbox(sandbox: Boolean) {
        synchronized(this) {
            this.isSandbox = sandbox
        }
    }

    fun setDeepLinkingEnabled(enabled: Boolean) {
        synchronized(this) {
            this.isDeepLinkingEnabled = enabled
        }
    }

    fun getLocale(): Locale? {
        return locale
    }

    @StyleRes
    fun getTheme(): Int {
        return theme
    }

    fun isSandbox(): Boolean {
        return isSandbox
    }

    fun isDeepLinkingEnabled(): Boolean {
        return isDeepLinkingEnabled
    }
}