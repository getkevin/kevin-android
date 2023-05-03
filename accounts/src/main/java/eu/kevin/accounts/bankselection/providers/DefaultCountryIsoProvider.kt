package eu.kevin.accounts.bankselection.providers

import android.content.Context
import android.content.pm.PackageManager
import android.telephony.TelephonyManager
import eu.kevin.common.extensions.getCurrentLocale

internal class DefaultCountryIsoProvider(
    private val context: Context,
    private val telephonyManager: TelephonyManager
) {
    fun getDefaultCountryIso(): String {
        if (
            context.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY) &&
            telephonyManager.phoneType != TelephonyManager.PHONE_TYPE_CDMA
        ) {
            return telephonyManager.networkCountryIso
        }

        return context.getCurrentLocale().language
    }
}