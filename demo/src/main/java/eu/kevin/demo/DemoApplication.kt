package eu.kevin.demo

import android.app.Application
import eu.kevin.accounts.KevinAccountsPlugin
import eu.kevin.core.plugin.Kevin
import eu.kevin.core.plugin.KevinConfiguration
import eu.kevin.inapppayments.KevinPaymentsPlugin
import java.util.*

class DemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Kevin.setLocale(Locale("lt"))
        Kevin.addPlugin(KevinAccountsPlugin)
        Kevin.addPlugin(KevinPaymentsPlugin)
        Kevin.configure(
            KevinConfiguration
                .builder()
                .setTheme(R.style.KevinTheme)
                .build()
        )
    }
}