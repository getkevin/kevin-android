package eu.kevin.demo

import android.app.Application
import eu.kevin.accounts.KevinAccountsPlugin
import eu.kevin.core.plugin.Kevin
import eu.kevin.core.plugin.KevinConfiguration
import eu.kevin.inapppayments.KevinPaymentsPlugin

class DemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()

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