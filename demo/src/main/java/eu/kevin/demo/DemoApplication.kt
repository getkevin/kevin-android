package eu.kevin.demo

import android.app.Application
import eu.kevin.accounts.KevinAccountsConfiguration
import eu.kevin.accounts.KevinAccountsPlugin
import eu.kevin.core.plugin.Kevin
import eu.kevin.inapppayments.KevinPaymentsConfiguration
import eu.kevin.inapppayments.KevinPaymentsPlugin
import java.util.*

class DemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Here you can set the SDK's locale
        // Default device's locale will be used if not set
        Kevin.setLocale(Locale("en"))
        Kevin.setTheme(R.style.KevinTheme)
        KevinAccountsPlugin.configure(
            KevinAccountsConfiguration.builder()
                .setCallbackUrl("https://redirect.kevin.eu/authorization.html")
                .build()
        )
        KevinPaymentsPlugin.configure(
            KevinPaymentsConfiguration.builder()
                .setCallbackUrl("https://redirect.kevin.eu/payment.html")
                .build()
        )
    }
}