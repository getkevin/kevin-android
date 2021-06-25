package eu.kevin.core.plugin

import androidx.annotation.StyleRes
import java.util.*

object Kevin {

    private var locale: Locale? = null
    private var theme: Int = 0

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

    fun getLocale(): Locale? {
        return locale
    }

    @StyleRes
    fun getTheme(): Int {
        return theme
    }
}