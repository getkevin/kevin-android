package eu.kevin.core.context

import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.LocaleList
import java.util.*

class KevinContextWrapper(baseContext: Context) : ContextWrapper(baseContext) {
    companion object {
        fun wrap(context: Context, newLocale: Locale): ContextWrapper {
            var newContext = context
            val configuration = newContext.resources.configuration
            configuration.setLocale(newLocale)
            Locale.setDefault(newLocale)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val localeList = LocaleList(newLocale)
                LocaleList.setDefault(localeList)
                configuration.setLocales(localeList)
            }

            newContext = newContext.createConfigurationContext(configuration)
            return ContextWrapper(newContext)
        }
    }
}