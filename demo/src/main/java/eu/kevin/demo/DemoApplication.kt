package eu.kevin.demo

import android.app.Application
import eu.kevin.accounts.KevinAccountsConfiguration
import eu.kevin.accounts.KevinAccountsPlugin
import eu.kevin.core.plugin.Kevin
import eu.kevin.inapppayments.KevinPaymentsConfiguration
import eu.kevin.inapppayments.KevinPaymentsPlugin
import java.util.Locale

internal class DemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // set SDK's locale
        // device's default locale will be used if not set
        Kevin.setLocale(Locale("en"))
        Kevin.setTheme(R.style.KevinTheme)
        Kevin.setDeepLinkingEnabled(true)
        KevinAccountsPlugin.configure(
            KevinAccountsConfiguration.builder()
                .setCallbackUrl("kevin://redirect.authorization")
                .build()
        )
        KevinPaymentsPlugin.configure(
            KevinPaymentsConfiguration.builder()
                .setCallbackUrl("kevin://redirect.payment")
                .build()
        )
    }
}