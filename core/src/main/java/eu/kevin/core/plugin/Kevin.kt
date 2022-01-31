package eu.kevin.core.plugin

import androidx.annotation.StyleRes
import eu.kevin.core.R
import java.util.*

object Kevin {

    private var locale: Locale? = null
    private var theme: Int? = null
    private var isSandbox: Boolean = false

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

    fun getLocale(): Locale? {
        return locale
    }

    @StyleRes
    fun getTheme(): Int {
        return theme ?: R.style.Theme_Kevin_Base
    }

    fun isSandbox(): Boolean {
        return isSandbox
    }
}