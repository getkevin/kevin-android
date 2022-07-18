package eu.kevin.core.plugin

import androidx.annotation.StyleRes
import eu.kevin.core.R
import java.util.Locale

object Kevin {

    private var locale: Locale? = null
    private var theme: Int = R.style.Theme_Kevin_Base
    private var isSandbox: Boolean = false
    private var isDeepLinkingEnabled: Boolean = false

    /**
     * Sets optional locale. Device's locale is used by default.
     *
     * This method is thread-safe.
     *
     * @param locale [Locale]
     */
    fun setLocale(locale: Locale?) {
        synchronized(this) {
            this.locale = locale
        }
    }

    /**
     * Sets optional, custom theme.
     * Theme.Kevin.Base is used by default.
     *
     * This method is thread-safe.
     *
     * @param theme theme's resId
     */
    fun setTheme(@StyleRes theme: Int) {
        synchronized(this) {
            this.theme = theme
        }
    }

    /**
     * Enables/disables Sandbox environment.
     *
     * This method is thread-safe.
     *
     * @param sandbox true if Sandbox should be used.
     */
    fun setSandbox(sandbox: Boolean) {
        synchronized(this) {
            this.isSandbox = sandbox
        }
    }

    /**
     * Enables/disables deep linking support.
     *
     * This method is thread-safe.
     *
     * @param enabled true if deep linking should be enabled
     */
    fun setDeepLinkingEnabled(enabled: Boolean) {
        synchronized(this) {
            this.isDeepLinkingEnabled = enabled
        }
    }

    /**
     * Returns current custom locale or null if it was not set.
     * @return [Locale]
     */
    fun getLocale(): Locale? {
        return locale
    }

    /**
     * Returns current custom theme or default one if it was not set.
     * @return theme's resId
     */
    @StyleRes
    fun getTheme(): Int {
        return theme
    }

    /**
     * Returns true if Sandbox environment is currently enabled - false otherwise.
     */
    fun isSandbox(): Boolean {
        return isSandbox
    }

    /**
     * Returns true if deep linking is currently enabled - false otherwise.
     */
    fun isDeepLinkingEnabled(): Boolean {
        return isDeepLinkingEnabled
    }
}